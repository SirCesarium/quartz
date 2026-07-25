package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.DiggerItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Tier
import net.sircesarium.qtz.api.item.ItemRegistry

fun ItemRegistry.axe(
    tier: Tier,
    name: String? = null,
    attackDamage: Float = 6f,
    attackSpeed: Float = -3.2f,
    configure: Item.Properties.() -> Unit = {},
) = toolProvider(
    name, tier,
    attributes = { DiggerItem.createAttributes(it, attackDamage, attackSpeed) },
    factory = { tier, props -> AxeItem(tier, props) },
    configure
)
