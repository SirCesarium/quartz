package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.DiggerItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.PickaxeItem
import net.minecraft.world.item.Tier
import net.sircesarium.qtz.api.item.ItemRegistry

fun ItemRegistry.pickaxe(
    tier: Tier,
    name: String? = null,
    attackDamage: Float = 1f,
    attackSpeed: Float = -2.8f,
    datagen: Boolean = true,
    configure: Item.Properties.() -> Unit = {},
) = toolProvider(
    name, tier,
    attributes = { DiggerItem.createAttributes(it, attackDamage, attackSpeed) },
    factory = { tier, props -> PickaxeItem(tier, props) },
    configure, datagen
)
