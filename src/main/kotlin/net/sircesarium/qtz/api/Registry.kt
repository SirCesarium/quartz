package net.sircesarium.qtz.api

import net.neoforged.bus.api.IEventBus

interface IRegistry {
    fun register(bus: IEventBus)
}