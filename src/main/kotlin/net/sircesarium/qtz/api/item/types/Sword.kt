package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.Item
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tier
import net.sircesarium.qtz.api.item.ItemRegistry
import net.sircesarium.qtz.api.item.QtzItem

fun ItemRegistry.sword(
    name: String? = null,
    tier: Tier,
    attackDamage: Int = 3,
    attackSpeed: Float = -2.4f,
    opts: QtzItem = QtzItem(),
    configure: Item.Properties.() -> Unit = {},
) = toolProvider(
    name, tier,
    attributes = { SwordItem.createAttributes(it, attackDamage, attackSpeed) },
    factory = { tier, props -> SwordItem(tier, props) },
    configure, opts
)
