package com.mel.meltributes.events.packet

import com.mel.meltributes.Meltributes
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent

object PacketEvent {

    @SubscribeEvent
    fun onJoinServer(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        event.manager.channel().pipeline().addAfter(
            "fml:packet_handler",
            "${Meltributes.MODID}_packet_handler",
            CustomChannelDuplexHandler()
        )
    }
}