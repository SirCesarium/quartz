package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.DiggerItem
import net.minecraft.world.item.HoeItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Tier
import net.sircesarium.qtz.api.item.ItemRegistry

fun ItemRegistry.hoe(
    tier: Tier,
    name: String? = null,
    attackDamage: Float = -1f,
    attackSpeed: Float = -3f,
    configure: Item.Properties.() -> Unit = {},
) = toolProvider(
    name, tier,
    attributes = { DiggerItem.createAttributes(it, attackDamage, attackSpeed) },
    factory = { tier, props -> HoeItem(tier, props) },
    configure
)
