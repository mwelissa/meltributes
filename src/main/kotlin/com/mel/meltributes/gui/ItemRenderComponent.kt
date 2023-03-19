package com.mel.meltributes.gui

import gg.essential.elementa.UIComponent
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import java.awt.Color
import kotlin.math.floor

class ItemRenderComponent(var item: ItemStack?, private val itemScale: Float, var price: String = "0"): UIComponent() {

    override fun draw(matrixStack: UMatrixStack) {
        this.beforeDraw(matrixStack)
        val x = (this.getLeft()/itemScale).toInt()
        val y = (this.getTop()/itemScale).toInt()
        UGraphics.GL.scale(itemScale, itemScale, 1.0f)
        renderItem(item, x, y, null)
        UGraphics.GL.scale(1.0f/itemScale, 1.0f/itemScale, 1.0f)

        /*if (this.isHovered()) {
            MeltributesGui.renderTooltip(item, getMousePosition().first.toInt(), getMousePosition().second.toInt())
        }*/

        super.draw(matrixStack)
    }

    private fun renderItem(stack: ItemStack?, x: Int, y: Int, text: String?) {
        val itemRender = Minecraft.getMinecraft().renderItem
        GlStateManager.enableRescaleNormal()
        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.enableDepth()
        itemRender.renderItemAndEffectIntoGUI(stack, x, y)
        //itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, stack, x, y, text)
        GlStateManager.disableDepth()
        GlStateManager.disableRescaleNormal()
    }
}


