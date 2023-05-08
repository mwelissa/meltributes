package com.mel.meltributes.gui


/*object MeltributesGui: WindowScreen(ElementaVersion.V1) {

    var itemComponentList = mutableListOf<UIBlock>()
    var attribute = ""
    var secondaryAttribute: String? = null
    var attributesAuctionHouse: MutableList<JsonObject> = mutableListOf()
    var arrayOfAttributes = arrayOf("mending", "lifeline", "blazing_resistance", "dominance", "ender_resistance", "breeze", "veteran", "mana_regeneration", "mana_pool", "experience", "blazing_fortune", "life_regeneration", "midas_touch", "undead", "speed", "fishing_speed", "arachno_resistance", "undead_resistance", "combo", "ender", "infection", "trophy_hunter", "double_hook", "attack_speed", "arachno", "elite", "mana_steal", "life_recovery", "fisherman", "fishing_experience", "ignition", "fortitude", "magic_find", "blazing", "deadeye", "hunter", "warrior")

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


    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        if (!textInput.hasFocus() && keyCode != 0) {
            if (!(keyCode == 14 && textInput.getText().isEmpty())) {
                textInput.grabWindowFocus()
                textInput.setActive(true)
                textInput.keyType(typedChar, keyCode)
            }
        }
        super.onKeyPressed(keyCode, typedChar, modifiers)

    }

    private val textInput = UITextInput("").constrain {
        x = 1.percent
        y = 1.percent
        width = 98.percent
        height = 3.percent
    }.childOf(searchBlock).apply {
        this.onKeyType { _, _ ->
            run {
                update()
            }
        }
    }

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
        numberOfAuctions.setText(attribute)
        Meltributes.launch {
            AuctionUtil.getAuctionHouse()
            if (auctionHouse != AuctionUtil.AuctionHouse) {
                auctionHouse = AuctionUtil.AuctionHouse
                update()
            }
            attributesAuctionHouse = mutableListOf()
            auctionHouse.forEach {
                if((it["bin"] as JsonPrimitive).boolean) {
                    val nbt = (it["item_bytes"] as JsonPrimitive).content.decodeItemBytes()
                    if (nbt.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getCompoundTag("attributes").hasKey(
                            attribute)) attributesAuctionHouse.add(it)
                }
            }
        }
        super.initScreen(width, height)
        if(itemComponentList.isEmpty()) {
            repeat(126) {
                val component = UIBlock(Color(20, 20, 20, 75)).constrain {
                    x = CramSiblingConstraint(padding = 8f)
                    y = CramSiblingConstraint(padding = 8f)
                    this.width = 36.pixels()
                    this.height = 36.pixels()
                }.apply {
                    onMouseClick {
                        (this.children[0] as ItemRenderComponent).id?.let {
                            Minecraft.getMinecraft().thePlayer.sendChatMessage("/viewauction $it")
                            println(it)
                        }
                    }
                }  childOf container
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
    }

    fun update() {
        val filteredList = attributesAuctionHouse.toMutableList().filter { (it["item_name"] as JsonPrimitive).content.lowercase().contains(textInput.getText().lowercase()) }.toMutableList()
        filteredList.sortBy {
            (it["starting_bid"] as JsonPrimitive).content.toLong() / 2.0.pow(
                (it["item_bytes"] as JsonPrimitive).content.        decodeItemBytes().getCompoundTag("tag")
                    .getCompoundTag("ExtraAttributes").getCompoundTag("attributes").getInteger(
                    attribute
                ) - 1
            )
        }
        val size = MathHelper.clamp_int(filteredList.size, 0, itemComponentList.size)

        for (i in 0 until itemComponentList.size) {
            itemComponentList[i].let {
                if(!it.children.isEmpty()) {
                    if (i < size) {
                        (it.children[0] as ItemRenderComponent).item = ItemStack.loadItemStackFromNBT((filteredList[i]["item_bytes"] as JsonPrimitive).content.decodeItemBytes())
                        (it.children[0] as ItemRenderComponent).id = (filteredList[i]["uuid"] as JsonPrimitive).content
                        (it.children[0] as ItemRenderComponent).tier = (filteredList[i]["item_bytes"] as JsonPrimitive).content.decodeItemBytes().getCompoundTag("tag").getCompoundTag("ExtraAttributes").getCompoundTag("attributes").getInteger(
                            attribute)
                        (it.children[1] as UIWrappedText).setText("§6" + compactDecimalFormat((filteredList[i]["starting_bid"] as JsonPrimitive).content.toLong() / 2.0.pow((filteredList[i]["item_bytes"] as JsonPrimitive).content.decodeItemBytes().getCompoundTag("tag").getCompoundTag("ExtraAttributes").getCompoundTag("attributes").getInteger(
                            attribute) - 1)))
                    } else {
                        (it.children[0] as ItemRenderComponent).item = null
                        (it.children[0] as ItemRenderComponent).id = null
                        (it.children[0] as ItemRenderComponent).tier = null
                        (it.children[1] as UIWrappedText).setText("")
                    }

                }
            }
        }


        *//*for ((i, item) in itemComponentList.withIndex()) {
            if(!item.children.isEmpty()) {
                if (attributesAuctionHouse.size > i) {
                    (item.children[0] as ItemRenderComponent).item = ItemStack.loadItemStackFromNBT((attributesAuctionHouse[i]["item_bytes"] as JsonPrimitive).content.decodeItemBytes())
                    (item.children[0] as ItemRenderComponent).id = (attributesAuctionHouse[i]["uuid"] as JsonPrimitive).content
                    (item.children[1] as UIWrappedText).setText("§6" + compactDecimalFormat((attributesAuctionHouse[i]["starting_bid"] as JsonPrimitive).content.toLong()))
                }
            }
        }*//*
        numberOfAuctions.setText(attributesAuctionHouse.size.toString())
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




    fun open() {
        EssentialAPI.getGuiUtil().openScreen(this)
    }

    fun renderTooltip(stack: ItemStack?, x: Int, y: Int) {
        renderToolTip(stack, x, y)
    }

}*/


