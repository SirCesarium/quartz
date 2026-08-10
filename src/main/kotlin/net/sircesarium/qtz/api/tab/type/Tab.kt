package net.sircesarium.qtz.api.tab.type

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.sircesarium.qtz.api.tab.TabItem
import net.sircesarium.qtz.api.tab.TabRegistry
import net.sircesarium.qtz.api.tab.TabScope
import net.sircesarium.qtz.utils.toSnakeCase
import java.util.function.Supplier

/**
 * Registers a creative mode tab named after the property (converted to snake_case).
 *
 * | Property | Config block | Example |
 * |---|---|---|
 * | Property name → registry name | `by tab()` → `"some_tab"` | |
 * | Title | `title` | `Component.translatable("itemGroup.modid.some_tab")` |
 * | Icon | `icon` (defaults to first item) | `icon = ModItems.someItem` |
 * | Order | `before`, `after` | `before = CreativeModeTabs.COMBAT` |
 * | Items | `+item`, `+tag`, `+block` | `+ModItems.someItem`, `+itemTag`, `+ModBlocks.someBlock` |
 * | FTS sections | `section {}` | `val some by section("Some", rgb(26, 26, 46), rgb(187, 170, 102)) { +ModItems.someItem }` |
 *
 * ```
 * class ModTabs : TabRegistry("modid") {
 *     val someTab by tab(before = CreativeModeTabs.COMBAT) {
 *         title = Component.translatable("itemGroup.modid.some_tab")
 *         icon = ModItems.someItem
 *
 *         +ModItems.someItem
 *         +ModBlocks.someBlock
 *
 *         val someSection by section("Some Section", rgb(26, 26, 46), rgb(187, 170, 102)) {
 *             +ModItems.someItem
 *             +itemTag
 *         }
 *     }
 * }
 * ```
 */
fun TabRegistry.tab(
    before: ResourceKey<CreativeModeTab>? = null,
    after: ResourceKey<CreativeModeTab>? = null,
    content: TabScope.() -> Unit = {},
) = bindName { rawName ->
    val name = rawName.toSnakeCase()
    val scope = TabScope(modId, name, before, after)
    scope.content()

    val def = scope.build()
    tabDefs.add(def)

    tabs.register(name, Supplier {
        val iconStack = def.icon?.asItem()?.defaultInstance
            ?: def.items.firstNotNullOfOrNull { (it as? TabItem.Entry)?.item }?.asItem()?.defaultInstance
            ?: ItemStack.EMPTY

        val builder = CreativeModeTab.builder()
            .title(def.title)
            .icon { iconStack }
            .displayItems { params, output ->
                val lookup = params.holders().lookupOrThrow(Registries.ITEM)

                for (entry in def.items) {
                    when (entry) {
                        is TabItem.Entry -> output.accept(entry.item)
                        is TabItem.Tag -> lookup.get(entry.tag).ifPresent { holders ->
                            for (holder in holders) output.accept(holder.value())
                        }
                    }
                }
            }

        if (def.before != null) builder.withTabsBefore(def.before)
        if (def.after != null) builder.withTabsAfter(def.after)

        builder.build()
    })
}
