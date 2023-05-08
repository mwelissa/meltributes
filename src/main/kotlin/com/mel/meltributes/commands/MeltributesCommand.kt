package com.mel.meltributes.commands

import com.mel.meltributes.Meltributes
import com.mel.meltributes.gui.MeltributesGui
import com.mel.meltributes.util.AttributeType
import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand
import kotlinx.serialization.json.*

object MeltributesCommand: Command("meltributes") {

    override val commandAliases = setOf(Alias("mel"), Alias("mt"))

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(MeltributesGui)
    }
}