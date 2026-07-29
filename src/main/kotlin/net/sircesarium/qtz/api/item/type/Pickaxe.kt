package net.sircesarium.qtz.api.item.type

import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.DiggerItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.PickaxeItem
import net.minecraft.world.item.Tier
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.registries.DeferredItem
import net.sircesarium.qtz.api.TagContext
import net.sircesarium.qtz.api.item.IItemPlan
import net.sircesarium.qtz.api.item.ItemRegistry
import net.sircesarium.qtz.api.item.ItemScope
import net.sircesarium.qtz.utils.toSnakeCase

class PickaxePlan @PublishedApi internal constructor(
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
            ItemTags.PICKAXES,
            ItemTags.MINING_ENCHANTABLE,
            ItemTags.MINING_LOOT_ENCHANTABLE,
            ItemTags.DURABILITY_ENCHANTABLE,
            ItemTags.VANISHING_ENCHANTABLE,
        ).forEach { context.add(it, deferredItem) }
    }
}

class PickaxeScope : ItemScope() {
    var attackDamage: Float = 1f
    var attackSpeed: Float = -2.8f
}

fun ItemRegistry.pickaxe(
    tier: Tier,
    config: PickaxeScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val scope = PickaxeScope().apply(config)

    val attrs = DiggerItem.createAttributes(tier, scope.attackDamage, scope.attackSpeed)
    val deferred = items.registerItem(name) {
        val props = Item.Properties().attributes(attrs)
            .apply { scope.propertiesCustomizer?.invoke(this) }
        PickaxeItem(tier, props)
    }

    val texture = ResourceLocation.fromNamespaceAndPath(modId, "item/${name}")
    val plan = PickaxePlan(name, deferred, texture).apply {
        model = scope.model
        customTexture = scope.customTexture
    }
    plans.add(plan)
    deferred
}
