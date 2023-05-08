package com.mel.meltributes.gui

import gg.essential.elementa.UIComponent
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack

class ItemRenderComponent(val stack: ItemStack): UIComponent() {

    private val itemScale by lazy { this.getWidth() / 16 }

    override fun draw(matrixStack: UMatrixStack) {
        this.beforeDraw(matrixStack)
        val x = (this.getLeft() / itemScale).toInt()
        val y = (this.getTop() / itemScale).toInt()

        UGraphics.GL.scale(itemScale, itemScale, 1.0f)
        GlStateManager.enableRescaleNormal()
        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.enableDepth()
        Minecraft.getMinecraft().renderItem.renderItemAndEffectIntoGUI(stack, x, y)
        //itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, stack, x, y, text)
        GlStateManager.disableDepth()
        GlStateManager.disableRescaleNormal()

        UGraphics.GL.scale(1.0f / itemScale, 1.0f / itemScale, 1.0f)

        /*if (this.isHovered()) {
            GuiScreen.ren
            MeltributesGui.renderTooltip(stack, getMousePosition().first.toInt(), getMousePosition().second.toInt())
        }*/

        super.draw(matrixStack)
    }
}


