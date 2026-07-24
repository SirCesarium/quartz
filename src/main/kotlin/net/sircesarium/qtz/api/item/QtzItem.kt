package net.sircesarium.qtz.api.item

import net.minecraft.world.item.Item

data class QtzItem(
    val creativeTab: String? = null,
    val itemOps: (Item.Properties.() -> Unit)? = null,
)
