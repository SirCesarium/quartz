package net.sircesarium.qtz.api.item.type

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.registries.DeferredItem
import net.sircesarium.qtz.api.item.IItemPlan
import net.sircesarium.qtz.api.item.ItemRegistry
import net.sircesarium.qtz.utils.toSnakeCase
import kotlin.jvm.JvmName

/**
 * Datagen plan for a simple item.
 *
 * Generates an `item/generated` model with `layer0` pointing to the
 * item's texture. The texture path defaults to `item/<id>.png`.
 */
class ItemPlan @PublishedApi internal constructor(
    private val id: String,
    val deferredItem: DeferredItem<Item>,
    private val defaultTexture: ResourceLocation,
) : IItemPlan {
    override val item: Item get() = deferredItem.get()
    var model: Boolean = true
    var customTexture: ResourceLocation? = null

    override fun emitItemModel(provider: ItemModelProvider) {
        if (!model) return
        val texture = customTexture ?: defaultTexture
        provider.singleTexture(id, provider.mcLoc("item/generated"), "layer0", texture)
    }
}

/**
 * Scope for configuring an [ItemPlan].
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Model generation | `model = true/false` | `model = false` — skip generating the item model |
 * | Custom texture | `texture("<modid>:item/<path>")` | `texture("qtz:item/sapphire")` |
 *
 * The `texture` function accepts full resource locations in `namespace:path` format.
 * By default it resolves to `<modId>:item/<name>`, i.e. `assets/<modId>/textures/item/<name>.png`.
 */
class ItemScope(private val plan: ItemPlan) {
    var model by plan::model
    var customTexture by plan::customTexture

    fun texture(path: String) {
        customTexture = ResourceLocation.parse(path)
    }
}

/**
 * Registers an item named after the property (converted to snake_case),
 * along with its corresponding item model datagen.
 *
 * The item model is generated as `item/<name>.json` with parent
 * `item/generated` and `layer0` pointing to `textures/item/<name>.png`.
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Property name → registry name | `by item()` → `"ruby"` |
 * | Model generation | `model = true/false` | `model = false` — omit the auto-generated model |
 * | Custom texture | `texture("<modid>:item/<path>")` | `texture("qtz:item/ruby_gem")` — overrides default `item/ruby.png` |
 *
 * Datagen is auto-generated — each item owns its plan.
 * Flags are per-item. Disabling one does not affect others.
 *
 * ```
 * object ModItems : ItemRegistry("qtz") {
 *     val ruby by item()
 *
 *     val sapphire by item {
 *         texture("qtz:item/sapphire")
 *         model = false
 *     }
 * }
 * ```
 */
@JvmName("itemSimple")
fun ItemRegistry.item(
    config: ItemScope.() -> Unit = {},
) = item<Item>(config)

/**
 * Registers an item using a custom item class.
 *
 * All config options from [item] apply.
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Custom item class | reified generic | `by item<MySpecialItem>()` |
 *
 * ```
 * class MySpecialItem(properties: Item.Properties) : Item(properties)
 *
 * object ModItems : ItemRegistry("qtz") {
 *     val special: DeferredItem<MySpecialItem> by item()
 * }
 * ```
 */
inline fun <reified I : Item> ItemRegistry.item(
    noinline config: ItemScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val deferred = items.registerItem(name) { Item(Item.Properties()) }
    val texture = ResourceLocation.fromNamespaceAndPath(modId, "item/${name}")
    val plan = ItemPlan(name, deferred, texture)
    ItemScope(plan).apply(config)
    plans.add(plan)
    @Suppress("UNCHECKED_CAST")
    deferred as DeferredItem<I>
}
