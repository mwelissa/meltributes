package com.mel.meltributes.gui

import com.mel.meltributes.Meltributes
import com.mel.meltributes.util.AttributeType
import com.mel.meltributes.util.Auction
import com.mel.meltributes.util.AuctionUtil
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.constraint
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.times
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.universal.UMatrixStack
import gg.essential.vigilance.gui.settings.DropDown
import gg.essential.vigilance.gui.settings.SelectorComponent
import java.awt.Color
import java.util.Collections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.minecraft.client.Minecraft
import org.lwjgl.input.Keyboard

object MeltributesGui: WindowScreen(ElementaVersion.V1, newGuiScale = 2) {

    val transparent = Color(0, 0, 0, 0)
    val outline = Color(75, 75, 75, 255)

    private val incoming = mutableSetOf<Auction>()
    private val auctions = Collections.synchronizedSet(mutableSetOf<AuctionRenderComponent>())
    private val auctionCache = Collections.synchronizedList(mutableListOf<AuctionRenderComponent>())
    private val attributeTypes = AttributeType.values()

    private var mainAttribute = AttributeType.UNDEAD_RESISTANCE
    private var secondaryAttribute: AttributeType? = null

    private var updating = false
    private var requiresUpdate = false
        set (value) {
            field = value
        }

    init {
        window.apply {
            this.constrain {
                color = Color(20, 20, 20, 75).constraint
            }
            this.onKeyType { c, key ->
                if (key != 0 && !searchComponent.isActive()) {
                    if (c.isLetterOrDigit() || key == Keyboard.KEY_BACK) {
                        searchComponent.grabWindowFocus()
                        if (c.isLetterOrDigit()) {
                            searchComponent.setText("$c")
                        } else {
                            if (isCtrlKeyDown()) {
                                searchComponent.setText("")
                            } else {
                                searchComponent.setText(searchComponent.getText().dropLast(1))
                            }
                        }
                    }
                }
            }
        }
        Meltributes.launch {
            for (channelSignal in AuctionUtil.auctionChannel) {
                when (channelSignal.id) {
                    0 -> {
                        updating = true
                        incoming.clear()
                    }
                    1 -> {
                        Minecraft.getMinecraft().isCallingFromMinecraftThread
                        if (incoming.add(channelSignal.extra as? Auction ?: continue)) {
                            withContext(Dispatchers.Default) {
                                auctions.add(AuctionRenderComponent(channelSignal.extra))
                            }
                        }
                    }
                    2 -> {
                        withContext(Dispatchers.Default) {
                            auctions.removeIf { it.auction !in incoming }
                        }
                        requiresUpdate = true
                        //updateCache()
                        updating = false
                        incoming.clear()
                    }
                    else -> {}
                }
            }
        }
    }

    private val sidebar = UIBlock(transparent).constrain {
        x = 0.pixels
        y = 0.pixels
        width = 16.percent
        height = 100.percent
    }.enableEffect(OutlineEffect(outline, 0.5f, sides = setOf(OutlineEffect.Side.Right), drawInsideChildren = true)) childOf window

    private val items = UIBlock(transparent).constrain {
        x = SiblingConstraint(0f)
        y = 0.pixels
        width = FillConstraint()
        height = 100.percent
    } childOf window

    private val searchComponent = UITextInput("Search...").constrain {
        x = 15.percent
        y = 5.percent
        width = 70.percent
        height = 11.pixels * 1.25
        textScale *= 1.25
    }.apply {
        this childOf sidebar
        this.onKeyType { _, _ ->
            requiresUpdate = true
            //updateCache()
        }
    }

    private val attributeOne = SelectorComponent(mainAttribute.ordinal, attributeTypes.map { it.display }).constrain {
        x = 15.percent - 5.pixels
        y = SiblingConstraint(10f)
        width = 70.percent
        textScale *= 1.25
    }.apply select@ {
        this effect ScissorEffect(UIBlock().apply { this.constraints = this@select.constraints })
        this childOf sidebar
        onValueChange {
            mainAttribute = attributeTypes[it as Int]
            requiresUpdate = true
            //updateCache()
        }
    }

    private val attributeTwo = SelectorComponent(0, listOf("None", *attributeTypes.map { it.display }.toTypedArray())).constrain {
        x = 15.percent - 5.pixels
        y = SiblingConstraint(10f)
        width = 70.percent
        textScale *= 1.25
    }.apply select@ {
        this effect ScissorEffect(UIBlock().apply { this.constraints = this@select.constraints })
        this childOf sidebar
        onValueChange {
            secondaryAttribute = attributeTypes.getOrNull(it as Int - 1)
            requiresUpdate = true
            //updateCache()
        }
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)

        if (requiresUpdate && !updating) {
            updateCache()
        }

        items.childrenOfType(AuctionRenderComponent::class.java).firstOrNull {
            it.itemComp.isHovered()
        }?.itemComp?.let {
            renderToolTip(it.stack, mouseX, mouseY)
        }
    }

    private fun updateCache() = runBlocking {
        withContext(Dispatchers.Default) {
            auctionCache.clear()
            auctionCache.addAll(auctions.filter {
                if (searchComponent.getText().contains("kuudra", true)) {
                    it.auction.isKuudraArmor && it.itemName.contains(
                        searchComponent.getText().replace("kuudra", "", true), true
                    )
                } else {
                    it.itemName.contains(searchComponent.getText(), true)
                }
            })

            auctionCache.removeIf { mainAttribute !in it.auction.attributes.map { it.type } }
            secondaryAttribute?.let { att ->
                auctionCache.removeIf { att !in it.auction.attributes.map { it.type } }
                auctionCache.sortBy { it.auction.price }
            } ?: run {
                auctionCache.sortBy { it.auction.attrPrices[mainAttribute] }
            }

            items.clearChildren()
            for (i in 0 until 15) {
                val comp = auctionCache.getOrNull(i) ?: break
                comp childOf items
            }

            requiresUpdate = false
        }
    }
}