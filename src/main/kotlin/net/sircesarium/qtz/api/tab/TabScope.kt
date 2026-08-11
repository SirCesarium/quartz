package net.sircesarium.qtz.api.tab

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import net.sircesarium.qtz.api.block.BlockWithItem
import net.sircesarium.qtz.utils.toSnakeCase
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

internal data class TabDef(
    val name: String,
    val title: Component,
    val icon: ItemLike?,
    val before: ResourceKey<CreativeModeTab>?,
    val after: ResourceKey<CreativeModeTab>?,
    val items: List<TabItem>,
    val sections: List<SectionDef>,
)

data class SectionDef(
    val name: String,
    val display: String,
    val banner: Int,
    val text: Int,
    val items: List<TabItem>,
)

sealed interface TabItem {
    data class Entry(val item: ItemLike) : TabItem
    data class Tag(val tag: TagKey<Item>) : TabItem
}

/**
 * Describes FTS sections to be applied to an existing creative tab registered by another mod.
 */
data class ExternalTabSectionsDef(
    val tab: ResourceKey<CreativeModeTab>,
    val sections: List<SectionDef>,
)

data class AddToDef(
    val tab: ResourceKey<CreativeModeTab>,
    val items: List<TabItem>,
)

/**
 * Describes one `removeFrom` block: the target tab and the items to remove from it.
 */
data class RemoveDef(
    val tab: ResourceKey<CreativeModeTab>,
    val items: List<TabItem>,
)

open class TabScope internal constructor(
    private val modId: String,
    internal val name: String,
    private val before: ResourceKey<CreativeModeTab>?,
    private val after: ResourceKey<CreativeModeTab>?,
) {
    var title: Component = Component.translatable("itemGroup.$modId.$name")
    var icon: ItemLike? = null

    private val items = mutableListOf<TabItem>()
    private val sections = mutableListOf<SectionDef>()

    operator fun ItemLike.unaryPlus() {
        items += TabItem.Entry(this)
    }

    operator fun TagKey<Item>.unaryPlus() {
        items += TabItem.Tag(this)
    }

    operator fun BlockWithItem<*, *>.unaryPlus() {
        items += TabItem.Entry(this.itemHolder)
    }

    fun section(
        display: String,
        banner: Int,
        text: Int,
        block: SectionScope.() -> Unit = {},
    ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, SectionDef>> = PropertyDelegateProvider { _, property ->
        val name = property.name.toSnakeCase()
        val scope = SectionScope()
        scope.block()

        val def = SectionDef(name, display, banner, text, scope.items.toList())
        sections += def
        ReadOnlyProperty { _, _ -> def }
    }

    internal fun build() = TabDef(name, title, icon, before, after, items.toList(), sections.toList())
}

class SectionScope {
    internal val items = mutableListOf<TabItem>()

    operator fun ItemLike.unaryPlus() {
        items += TabItem.Entry(this)
    }

    operator fun TagKey<Item>.unaryPlus() {
        items += TabItem.Tag(this)
    }

    operator fun BlockWithItem<*, *>.unaryPlus() {
        items += TabItem.Entry(this.itemHolder)
    }
}

/**
 * Scope used inside a `removeFrom` block. Start a line with `-` followed by an item, tag or block
 * to remove it from the target tab.
 */
class RemoveScope {
    internal val items = mutableListOf<TabItem>()

    operator fun ItemLike.unaryMinus() {
        items += TabItem.Entry(this)
    }

    operator fun TagKey<Item>.unaryMinus() {
        items += TabItem.Tag(this)
    }

    operator fun BlockWithItem<*, *>.unaryMinus() {
        items += TabItem.Entry(this.itemHolder)
    }
}

/**
 * Scope used inside a `sectionsOn` block. Declare FTS sections to be applied to an existing
 * creative tab, using the same `section` DSL as a regular tab.
 */
class ExternalTabSectionsScope {
    internal val sections = mutableListOf<SectionDef>()

    fun section(
        display: String,
        banner: Int,
        text: Int,
        block: SectionScope.() -> Unit = {},
    ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, SectionDef>> = PropertyDelegateProvider { _, property ->
        val name = property.name.toSnakeCase()
        val scope = SectionScope()
        scope.block()

        val def = SectionDef(name, display, banner, text, scope.items.toList())
        sections += def
        ReadOnlyProperty { _, _ -> def }
    }
}
