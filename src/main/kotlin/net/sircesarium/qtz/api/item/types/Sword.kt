package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.Item
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tier
import net.sircesarium.qtz.api.item.ItemRegistry

fun ItemRegistry.sword(
    tier: Tier,
    name: String? = null,
    attackDamage: Float = 3f,
    attackSpeed: Float = -2.4f,
    configure: Item.Properties.() -> Unit = {},
) = toolProvider(
    name, tier,
    attributes = { SwordItem.createAttributes(it, attackDamage, attackSpeed) },
    factory = { tier, props -> SwordItem(tier, props) },
    configure
)
