package net.sircesarium.qtz.api.item.type

import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.DiggerItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Tier
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.registries.DeferredItem
import net.sircesarium.qtz.api.TagContext
import net.sircesarium.qtz.api.item.IItemPlan
import net.sircesarium.qtz.api.item.ItemRegistry
import net.sircesarium.qtz.api.item.ItemScope
import net.sircesarium.qtz.utils.toSnakeCase

class AxePlan @PublishedApi internal constructor(
    private val id: String,
    val deferredItem: DeferredItem<out Item>,
    private val defaultTexture: ResourceLocation,
) : IItemPlan {
    override val item: Item get() = deferredItem.get()
    var model: Boolean = true
    var customTexture: ResourceLocation? = null

    override fun emitItemModel(provider: ItemModelProvider) {
        if (!model) return
        val texture = customTexture ?: defaultTexture
        provider.singleTexture(id, provider.mcLoc("item/handheld"), "layer0", texture)
    }

    override fun emitTags(context: TagContext) {
        listOf(
            ItemTags.AXES,
            ItemTags.MINING_ENCHANTABLE,
            ItemTags.WEAPON_ENCHANTABLE,
            ItemTags.DURABILITY_ENCHANTABLE,
            ItemTags.VANISHING_ENCHANTABLE,
        ).forEach { context.add(it, deferredItem) }
    }
}

class AxeScope : ItemScope() {
    var attackDamage: Float = 6f
    var attackSpeed: Float = -3.0f
}

fun ItemRegistry.axe(
    tier: Tier,
    config: AxeScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val scope = AxeScope().apply(config)

    val attrs = DiggerItem.createAttributes(tier, scope.attackDamage, scope.attackSpeed)
    val deferred = items.registerItem(name) {
        val props = Item.Properties().attributes(attrs)
            .apply { scope.propertiesCustomizer?.invoke(this) }
        AxeItem(tier, props)
    }

    val texture = ResourceLocation.fromNamespaceAndPath(modId, "item/${name}")
    val plan = AxePlan(name, deferred, texture).apply {
        model = scope.model
        customTexture = scope.customTexture
    }
    plans.add(plan)
    deferred
}
