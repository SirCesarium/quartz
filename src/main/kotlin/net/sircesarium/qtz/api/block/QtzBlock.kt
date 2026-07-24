package net.sircesarium.qtz.api.block

import net.minecraft.world.item.Item

data class QtzBlock(
    val withItem: Boolean = true,
    val creativeTab: String? = null,
    val itemOps: (Item.Properties.() -> Unit)? = null,
)
