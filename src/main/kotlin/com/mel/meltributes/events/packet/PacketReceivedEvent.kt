package com.mel.meltributes.events.packet

import net.minecraft.network.Packet
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

@Cancelable
class PacketReceivedEvent(val packet: Packet<*>): Event()