package com.mel.meltributes.gui

import com.mel.meltributes.util.Auction
import com.mel.meltributes.util.compactDecimalFormat
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.AspectConstraint
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.CramSiblingConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.RelativeWindowConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.coerceAtLeast
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.universal.UMatrixStack
import java.awt.Color
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.StringUtils

class AuctionRenderComponent(val auction: Auction): UIComponent() {

    val itemName: String = StringUtils.stripControlCodes(auction.item.displayName)
    private val outline = OutlineEffect(
        MeltributesGui.outline,
        0.5f,
    )

    init {
        constrain {
            x = CramSiblingConstraint(0.5f)
            y = CramSiblingConstraint(0.5f)
            width = RelativeConstraint(1 / 3f) - 0.5.pixels
            height = RelativeWindowConstraint(1 / 5f)
        } effect outline// effect ScissorEffect() - somehow prevents the rendering of everything but the item icon, maybe the bounds have to be corrected?
    }

    val itemBlock = UIBlock(MeltributesGui.transparent).constrain {
        x = 0.pixels
        y = 0.pixels
        width = AspectConstraint(1f)
        height = 100.percent
    } childOf this

    val itemComp = ItemRenderComponent(auction.item).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 60.percent
        height = 60.percent
    } childOf itemBlock

    val dataBlock = UIBlock(MeltributesGui.transparent).constrain {
        x = SiblingConstraint(0f)
        y = 0.pixels
        width = FillConstraint()
        height = FillConstraint()
    } childOf this

    val nameComp = UIText(auction.item.displayName).constrain {
        x = 0.pixels
        y = 5.pixels
        textScale = 1.25f.pixels
    } childOf dataBlock

    val priceComp = UIText("Price: ${auction.price.compactDecimalFormat()}").constrain {
        x = 0.pixels
        y = SiblingConstraint(5f)
        textScale = 1.25f.pixels
    } childOf dataBlock

    init {
        auction.attributes.forEach {
            val text = "${it.type?.display ?: "null"} ${it.level}" + if (it.level > 1) {
                " (${auction.attrPrices[it.type]?.compactDecimalFormat() ?: 0} per)"
            } else {
                ""
            }
            UIText(text).constrain {
                x = 0.pixels
                y = SiblingConstraint(5f)
            } childOf dataBlock
        }

        onMouseEnter {
            outline.color = Color.WHITE
            outline.drawInsideChildren = true
        }
        onMouseLeave {
            outline.color = MeltributesGui.outline
            outline.drawInsideChildren = false
        }
        onMouseClick {
            outline.color = MeltributesGui.outline
            outline.drawInsideChildren = false
            if (it.mouseButton == 0) {
                val command = "/viewauction ${auction.id}"
                if (GuiScreen.isCtrlKeyDown()) {
                    GuiScreen.setClipboardString(command)
                } else {
                    Minecraft.getMinecraft().thePlayer?.sendChatMessage(command)
                }
            }
        }
    }

    override fun draw(matrixStack: UMatrixStack) {
        this.beforeDraw(matrixStack)

        super.draw(matrixStack)
    }

    override fun equals(other: Any?): Boolean {
        return this.auction == (other as? AuctionRenderComponent)?.auction
    }

    override fun hashCode(): Int {
        return auction.hashCode()
    }
}