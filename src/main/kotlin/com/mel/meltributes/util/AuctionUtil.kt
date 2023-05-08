@file:OptIn(ExperimentalCoroutinesApi::class)

package com.mel.meltributes.util

import com.mel.meltributes.Meltributes
import kotlinx.serialization.json.*
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay

object AuctionUtil {

    private const val API_URL = "https://api.hypixel.net/skyblock/auctions?page="
    private val UPDATE_START = ChannelSignal(0)
    private val UPDATE_END = ChannelSignal(2)
    var lastUpdated = -1L

    val auctionChannel = Meltributes.produce(Meltributes.coroutineContext, Int.MAX_VALUE) {
        while (true) {
            try {
                val res = Json.parseToJsonElement(URL("${API_URL}0").readText()).jsonObject
                if (res["success"]?.jsonPrimitive?.boolean != true) {
                    println("Hypixel API didn't like that! (${res["cause"]?.jsonPrimitive?.content})")
                    delay(10 * 1000L)
                }
                val last = res["lastUpdated"]!!.jsonPrimitive.long
                if (last > lastUpdated) {
                    // Fuck it we ball
                    lastUpdated = last
                    send(UPDATE_START)
                    val pages = res["totalPages"]!!.jsonPrimitive.int
                    List(pages - 1) {
                        async {
                            if (it == 0) {
                                processItems(res["auctions"]?.jsonArray ?: JsonArray(emptyList()))
                            } else {
                                val page = Json.parseToJsonElement(URL("$API_URL$it").readText()).jsonObject
                                processItems(page["auctions"]?.jsonArray ?: JsonArray(emptyList()))
                            }
                        }
                    }.awaitAll()
                    send(UPDATE_END)
                    delay(System.currentTimeMillis() - lastUpdated + 2 * 60 * 1000L)
                } else {
                    // We do not. We don't ball.
                    delay(10 * 1000L)
                }
            } catch (e: Exception) {
                println("Exception while trying to download AH: ${e.javaClass.simpleName} (${e.message})")
                delay(10 * 1000L)
            }
        }
    }

    /*fun getAuctionHouse() {
        if (System.currentTimeMillis() - lastReset < 60000L) {
            Meltributes.chat("You need to wait ${60000L - (System.currentTimeMillis() - lastReset)}ms")
            return
        }
        lastReset = System.currentTimeMillis()
        val returnList = mutableListOf<JsonObject>()
        val firstPage = Json.decodeFromString<JsonObject>(getAuctionHousePage(0))
        if (!(firstPage["success"] as JsonPrimitive).boolean) return Meltributes.chat("Error")
        (firstPage["auctions"] as JsonArray).forEach { returnList.add(it as JsonObject) }
        val totalPages = (firstPage["totalPages"] as JsonPrimitive).intOrNull ?: return Meltributes.chat("Error")
        for (i in 1 until totalPages) {
            val page = Json.decodeFromString<JsonObject>(getAuctionHousePage(i))
            (page["auctions"] as JsonArray).forEach { returnList.add(it as JsonObject) }
        }
        //AuctionHouse = returnList
        //Meltributes.chat("Finished getting Auction House")
    }*/

    private suspend fun ProducerScope<ChannelSignal>.processItems(auctions: JsonArray) {
        for (auction in auctions) {
            if (auction.jsonObject["bin"]?.jsonPrimitive?.booleanOrNull != true) continue
            val itemID = auction.jsonObject["item_uuid"]?.jsonPrimitive?.content ?: continue

            val raw = auction.jsonObject["item_bytes"]?.jsonPrimitive?.content ?: continue

            val attTag = raw.decodeItemBytes().getCompoundTag("tag").getCompoundTag("ExtraAttributes").getCompoundTag("attributes")
            val attributes = attTag.keySet.map { Attribute(AttributeType.fromID(it), attTag.getInteger(it)) }
            if (attributes.isEmpty()) continue

            val auctionID = auction.jsonObject["uuid"]?.jsonPrimitive?.content ?: continue
            val price = auction.jsonObject["starting_bid"]?.jsonPrimitive?.longOrNull ?: continue

            send(ChannelSignal(1, Auction(auctionID, price, itemID, raw, attributes)))
        }
    }

    private fun getAuctionHousePage(page: Int): String {
        var returnValue = ""
        val url = URL("https://api.hypixel.net/skyblock/auctions?page=${page}")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    returnValue += line
                }
            }
        }
        return returnValue
    }
}