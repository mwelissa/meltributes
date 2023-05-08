package com.mel.meltributes.util

import java.io.ByteArrayInputStream
import java.text.DecimalFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.client.config.GuiUtils
import org.apache.commons.codec.binary.Base64
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun String.decodeItemBytes(): NBTTagCompound {
    return (CompressedStreamTools.readCompressed(ByteArrayInputStream(Base64.decodeBase64(this)))
        .getTag("i") as NBTTagList).getCompoundTagAt(0)
}

private val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
private val mc = Minecraft.getMinecraft()

fun Number.compactDecimalFormat(): String {

    val numValue = this.toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3

    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0.0").format(
            numValue / 10.0.pow((base * 3).toDouble())
        ) + suffix[base]
    } else {
        DecimalFormat("#,##0").format(numValue)
    }
}

fun String.containsAny(vararg text: String) = text.any { it in this }

data class ChannelSignal(val id: Int, val extra: Any? = null)
// 0 -> Update start
// 1 -> Item
// 2 -> Update end

data class Auction(val id: String, val price: Long, val itemID: String, val raw: String, val attributes: List<Attribute>) {

    val item by lazy { ItemStack.loadItemStackFromNBT(raw.decodeItemBytes()) }
    val isKuudraArmor by lazy {
        item.displayName.containsAny("Aurora", "Crimson", "Fervor", "Hollow", "Terror") &&
                item.displayName.containsAny("Helmet", "Chestplate", "Leggings", "Boots")
    }

    val attrPrices by lazy {
        buildMap {
            attributes.forEach {
                put(it.type, (price / 2.0.pow(it.level - 1)).toLong())
            }
        }
    }
}