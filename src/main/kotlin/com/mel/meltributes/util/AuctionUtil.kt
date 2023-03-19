package com.mel.meltributes.util



import com.mel.meltributes.Meltributes
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import sun.net.www.http.HttpClient
import java.net.HttpURLConnection
import java.net.URL
import kotlin.streams.toList

object AuctionUtil {
    var lastReset = 0L
    var AuctionHouse: List<JsonObject> = mutableListOf()
    fun getAuctionHouse() {
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
        AuctionHouse = returnList
        Meltributes.chat("Finished getting Auction House")
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