package com.mel.meltributes

import com.mel.meltributes.commands.MeltributesCommand
import com.mel.meltributes.core.Config
import com.mel.meltributes.events.packet.PacketEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

@Mod(
    modid = Meltributes.MODID,
    version = Meltributes.VERSION,
    name = Meltributes.NAME,
    clientSideOnly = true,
    acceptedMinecraftVersions = "[1.8.9]",
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object Meltributes: CoroutineScope {
    const val NAME = "meltributes"
    const val MODID = "meltributes"
    const val VERSION = "1.0"

    override val coroutineContext: CoroutineContext = Executors.newFixedThreadPool(10).asCoroutineDispatcher() + SupervisorJob()

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        Config.preload()
        MeltributesCommand.register()
        MinecraftForge.EVENT_BUS.register(PacketEvent)
    }

    fun chat(message: Any) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText("§f[§5Meltributes§r] $message"))
    }
}