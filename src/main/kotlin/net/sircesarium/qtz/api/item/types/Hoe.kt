package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.DiggerItem
import net.minecraft.world.item.HoeItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Tier
import net.sircesarium.qtz.api.item.ItemRegistry
import net.sircesarium.qtz.api.item.QtzItem

fun ItemRegistry.hoe(
    name: String? = null,
    tier: Tier,
    attackDamage: Float = -1f,
    attackSpeed: Float = -3f,
    opts: QtzItem = QtzItem(),
    configure: Item.Properties.() -> Unit = {},
) = toolProvider(
    name, tier,
    attributes = { DiggerItem.createAttributes(it, attackDamage, attackSpeed) },
    factory = { tier, props -> HoeItem(tier, props) },
    configure, opts
)
