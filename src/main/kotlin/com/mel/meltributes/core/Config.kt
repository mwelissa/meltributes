package com.mel.meltributes.core

import com.mel.meltributes.Meltributes
import gg.essential.vigilance.Vigilant
import java.io.File

object Config: Vigilant(File("./config/${Meltributes.MODID}config.toml")) {

    init {
        initialize()
    }
}