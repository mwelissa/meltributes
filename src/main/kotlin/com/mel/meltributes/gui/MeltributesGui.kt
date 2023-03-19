package com.mel.meltributes.gui


import com.ibm.icu.text.CompactDecimalFormat
import com.ibm.icu.util.ULocale
import com.mel.meltributes.Meltributes
import com.mel.meltributes.gui.MeltributesGui.decodeItemBytes
import com.mel.meltributes.util.AuctionUtil
import gg.essential.api.EssentialAPI
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.CramSiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import org.apache.commons.codec.binary.Base64
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.io.ByteArrayInputStream
import java.text.DecimalFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

object MeltributesGui: WindowScreen(ElementaVersion.V1) {

    var itemComponentList = mutableListOf<UIBlock>()

    var attributesAuctionHouse: MutableList<JsonObject> = mutableListOf()


    private val backgroundBlock = UIBlock(Color(20, 20, 20, 75)).constrain {
        x = 5.percent
        y = 5.percent
        width = 90.percent
        height = 90.percent
    } childOf window

    private var numberOfAuctions = UIText("a", true).constrain {
        x = 0.pixels
        y = 0.pixels
    } childOf backgroundBlock


    private val searchBlock = UIBlock(Color(20, 20, 20, 75)).constrain {
        x = 5.percent
        y = 5.percent
        width = 15.percent
        height = 90.percent
    } childOf backgroundBlock

    private val containerBlock = UIBlock(Color(20, 20, 20, 75)).constrain {
        x = 22.percent
        y = 5.percent
        width = 73.percent
        height = 90.percent
    } childOf backgroundBlock


    private val container = UIContainer().constrain {
        x = 1.5f.percent
        y = 1.5f.percent
        width = 97.percent
        height = 97.percent
    } childOf containerBlock

    var auctionHouse = AuctionUtil.AuctionHouse

    override fun initScreen(width: Int, height: Int) {
        Meltributes.launch {
            AuctionUtil.getAuctionHouse()
            if (auctionHouse != AuctionUtil.AuctionHouse) {
                auctionHouse = AuctionUtil.AuctionHouse
                attributesAuctionHouse = mutableListOf()
                auctionHouse.forEach {
                    val nbt = (it["item_bytes"] as JsonPrimitive).content.decodeItemBytes()
                    val flag = nbt.getCompoundTag("tag").getCompoundTag("ExtraAttributes").hasKey("attributes")
                    if (flag) attributesAuctionHouse.add(it)
                }
            }
        }
        super.initScreen(width, height)
        repeat(126) {
            val component = UIBlock(Color(20, 20, 20, 75)).constrain {
                x = CramSiblingConstraint(padding = 8f)
                y = CramSiblingConstraint(padding = 8f)
                this.width = 36.pixels()
                this.height = 36.pixels()
            } childOf container
            ItemRenderComponent(null, 2.17f).constrain {
                x = 5.percent
                y = 5.percent
                this.width = 90.percent
                this.height = 90.percent
            } childOf component
            UIWrappedText("", true, centered = true).constrain {
                x = 0.percent
                y = 100.percent
                this.width = 100.percent
                this.height = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT.pixels
            } childOf component
            itemComponentList.add(component)
        }
    }

    override fun onTick() {
        for ((i, item) in itemComponentList.withIndex()) {
            if(!item.children.isEmpty()) {
                if (attributesAuctionHouse.size > i) {
                    (item.children[0] as ItemRenderComponent).item = ItemStack.loadItemStackFromNBT((attributesAuctionHouse[i]["item_bytes"] as JsonPrimitive).content.decodeItemBytes())
                    (item.children[1] as UIWrappedText).setText("§6" + compactDecimalFormat((attributesAuctionHouse[i]["starting_bid"] as JsonPrimitive).content.toLong()))
                }
            }
        }
        numberOfAuctions.setText(attributesAuctionHouse.size.toString())
        super.onTick()
    }

    fun compactDecimalFormat(number: Number): String {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')

        val numValue = number.toLong()
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

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
        for (i in itemComponentList) {
            if (i.isHovered()) {
                if (!i.children.isEmpty()) {
                    renderTooltip((i.children[0] as ItemRenderComponent).item ?: return, mouseX, mouseY)
                }
            }
        }
    }


    private fun String.decodeItemBytes(): NBTTagCompound {
        return (CompressedStreamTools.readCompressed(ByteArrayInputStream(Base64.decodeBase64(this)))
            .getTag("i") as NBTTagList).getCompoundTagAt(0)
    }


    fun open() {
        EssentialAPI.getGuiUtil().openScreen(this)
    }

    fun renderTooltip(stack: ItemStack?, x: Int, y: Int) {
        renderToolTip(stack, x, y)
    }

}


