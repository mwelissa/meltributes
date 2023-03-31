package com.mel.meltributes.commands

import com.mel.meltributes.Meltributes
import com.mel.meltributes.gui.MeltributesGui
import com.mel.meltributes.util.AuctionUtil
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTException
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ResourceLocation
import org.apache.commons.codec.binary.Base64
import java.io.ByteArrayInputStream
import java.util.Arrays


object MeltributesCommand: Command("meltributes") {

    @SubCommand("list")
    fun list() {
        Meltributes.chat(MeltributesGui.arrayOfAttributes.contentToString())
    }

    @DefaultHandler
    fun handle(attribute: String, secondaryAttribute: String?) {
       /*
        Meltributes.launch {
            var auctionHouse = AuctionUtil.getAuctionHouse()
            if (auctionHouse.isEmpty()) {
                Meltributes.chat("There was an error getting the auction house.")
                return@launch
            }


            /*auctionHouse.forEach {
                it["item_bytes"]?.let { bytes ->
                    val nbt = (bytes as JsonPrimitive).content.decodeItemBytes()
                    nbtList.add(nbt)
                }
            }*/
            auctionHouse = auctionHouse.filter {
                (it["bin"] as JsonPrimitive).boolean && !(it["claimed"] as JsonPrimitive).boolean
            }
            println(auctionHouse.first { (it["item_name"] as JsonPrimitive).content.contains("Attribute") })
            auctionHouse = auctionHouse.filter { (it["item_name"] as JsonPrimitive).content.contains("Attribute Shard") }.filter {
                it["item_bytes"].let { bytes ->
                    val nbt = (bytes as JsonPrimitive).content.decodeItemBytes()
                    Minecraft.getMinecraft().thePlayer.inventory.addItemStackToInventory(
                        ItemStack.loadItemStackFromNBT(
                            nbt
                        )
                    )
                    nbt.getCompoundTag("tag").getCompoundTag("ExtraAttributes").hasKey("attributes")
                }
            }
            println(auctionHouse)


        }
        */
        MeltributesGui.attribute = if (attribute in MeltributesGui.arrayOfAttributes) attribute else {
            Meltributes.chat("Not an attribute id (/meltributes list)")
            return
        }
        MeltributesGui.secondaryAttribute = if (secondaryAttribute in MeltributesGui.arrayOfAttributes) secondaryAttribute else null

        MeltributesGui.open()
    }
}