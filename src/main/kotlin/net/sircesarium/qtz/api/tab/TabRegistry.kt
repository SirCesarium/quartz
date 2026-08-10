package net.sircesarium.qtz.api.tab

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModList
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.sircesarium.qtz.api.IRegistry
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

open class TabRegistry(val modId: String) : IRegistry {
    @PublishedApi internal val tabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId)
    @PublishedApi internal val tabDefs = mutableListOf<TabDef>()
    @PublishedApi internal val addToDefs = mutableListOf<AddToDef>()
    @PublishedApi internal val removeToDefs = mutableListOf<RemoveDef>()

    companion object {
        val instances = mutableListOf<TabRegistry>()
    }

    init {
        instances.add(this)
    }

    override fun register(bus: IEventBus) {
        tabs.register(bus)
        if (ModList.get().isLoaded("fancytabsections")) {
            FTSAdapter.apply(modId, tabDefs)
        }
        if (addToDefs.isNotEmpty() || removeToDefs.isNotEmpty()) {
            bus.addListener(::onBuildContents)
        }
    }

    private fun onBuildContents(event: BuildCreativeModeTabContentsEvent) {
        val lookup = event.parameters.holders().lookupOrThrow(Registries.ITEM)

        for (def in addToDefs) {
            if (event.tabKey != def.tab) continue

            for (item in def.items) {
                when (item) {
                    is TabItem.Entry -> event.accept(item.item)
                    is TabItem.Tag -> lookup.get(item.tag).ifPresent { holders ->
                        for (holder in holders) event.accept(holder.value())
                    }
                }
            }
        }

        for (def in removeToDefs) {
            if (event.tabKey != def.tab) continue

            for (item in def.items) {
                val entries = buildList {
                    addAll(event.parentEntries)
                    addAll(event.searchEntries)
                }

                val toRemove = when (item) {
                    is TabItem.Entry -> entries.filter { it.`is`(item.item.asItem()) }
                    is TabItem.Tag -> entries.filter { it.`is`(item.tag) }
                }

                for (stack in toRemove) {
                    event.remove(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)
                }
            }
        }
    }

    /**
     * Adds items to an existing creative mode tab.
     *
     * | Property | Config block | Example |
     * |---|---|---|
     * | Target tab | `tab` | `CreativeModeTabs.BUILDING_BLOCKS` |
     * | Items | `+item`, `+tag`, `+block` | `+ModItems.someItem`, `+itemTag`, `+ModBlocks.someBlock` |
     *
     * ```
     * class ModTabs : TabRegistry("modid") {
     *     val someBlockInBuilding by addTo(CreativeModeTabs.BUILDING_BLOCKS) {
     *         +ModBlocks.someBlock
     *         +itemTag
     *     }
     * }
     * ```
     */
    fun addTo(
        tab: ResourceKey<CreativeModeTab>,
        content: SectionScope.() -> Unit = {},
    ) = bindName { _ ->
        val scope = SectionScope()
        scope.content()

        val def = AddToDef(tab, scope.items.toList())
        addToDefs.add(def)
        def
    }

    /**
     * Removes items from an existing creative mode tab.
     *
     * | Property | Config block | Example |
     * |---|---|---|
     * | Target tab | `tab` | `CreativeModeTabs.BUILDING_BLOCKS` |
     * | Items | `-item`, `-tag`, `-block` | `-ModItems.someItem`, `-itemTag`, `-ModBlocks.someBlock` |
     *
     * ```
     * class ModTabs : TabRegistry("modid") {
     *     val noRedstoneInBuilding by removeFrom(CreativeModeTabs.BUILDING_BLOCKS) {
     *         -ModItems.redstone
     *         -someItemTag
     *     }
     * }
     * ```
     */
    fun removeFrom(
        tab: ResourceKey<CreativeModeTab>,
        content: RemoveScope.() -> Unit = {},
    ) = bindName { _ ->
        val scope = RemoveScope()
        scope.content()

        val def = RemoveDef(tab, scope.items.toList())
        removeToDefs.add(def)
        def
    }

    fun <T> bindName(factory: (String) -> T) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val instance = factory(property.name)
        ReadOnlyProperty { _, _ -> instance }
    }
}
