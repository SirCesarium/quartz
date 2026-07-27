package net.sircesarium.qtz.api.item.types

import net.minecraft.world.item.Item
import net.minecraft.world.item.Tier
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.sircesarium.qtz.api.item.provider.ItemProvider
import net.sircesarium.qtz.api.item.ItemRegistry

internal fun <T : Item> ItemRegistry.toolProvider(
    name: String?,
    tier: Tier,
    attributes: (Tier) -> ItemAttributeModifiers,
    factory: (Tier, Item.Properties) -> T,
    configure: Item.Properties.() -> Unit,
    datagen: Boolean = true,
) = ItemProvider(
    registry = this, name,
    factory = { props -> factory(tier, props.attributes(attributes(tier))) },
    configure = { stacksTo(1); configure() },
    onRegister = if (datagen) { itemName -> this.handheldModels.add(this.modId to itemName) } else null
)
