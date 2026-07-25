package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.Item
import net.minecraft.world.item.Tier
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.sircesarium.qtz.api.item.ItemProvider
import net.sircesarium.qtz.api.item.ItemRegistry
import net.sircesarium.qtz.api.item.QtzItem

internal fun <T : Item> ItemRegistry.toolProvider(
    name: String?,
    tier: Tier,
    attributes: (Tier) -> ItemAttributeModifiers,
    factory: (Tier, Item.Properties) -> T,
    configure: Item.Properties.() -> Unit,
    opts: QtzItem,
) = ItemProvider(
    registry = this, name,
    factory = { props -> factory(tier, props.attributes(attributes(tier))) },
    configure = { stacksTo(1); configure() },
    opts
)
