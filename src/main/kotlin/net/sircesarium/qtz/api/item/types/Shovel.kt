package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.DiggerItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ShovelItem
import net.minecraft.world.item.Tier
import net.sircesarium.qtz.api.item.ItemRegistry
import net.sircesarium.qtz.api.item.QtzItem

fun ItemRegistry.shovel(
    name: String? = null,
    tier: Tier,
    attackDamage: Float = 1.5f,
    attackSpeed: Float = -3.0f,
    opts: QtzItem = QtzItem(),
    configure: Item.Properties.() -> Unit = {},
) = toolProvider(
    name, tier,
    attributes = { DiggerItem.createAttributes(it, attackDamage, attackSpeed) },
    factory = { tier, props -> ShovelItem(tier, props) },
    configure, opts
)
