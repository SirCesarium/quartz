package net.sircesarium.qtz.datagen

import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.item.ItemRegistry
import net.sircesarium.qtz.api.sound.SoundRegistry

object QuartzDataGatherers {
    fun register(modEventBus: IEventBus) {
        modEventBus.addListener(QuartzDataGatherers::gatherData)
    }

    internal fun gatherData(event: GatherDataEvent) {
        val output = event.generator.packOutput
        val lookup = event.lookupProvider
        val helper = event.existingFileHelper

        for ((modId, registries) in BlockRegistry.instances.groupBy { it.modId }) {
            val plans = registries.flatMap { it.plans }

            event.generator.addProvider(
                true,
                QuartzBlockStateProvider(output, modId, helper, plans)
            )

            event.generator.addProvider(
                true,
                LootTableProvider(
                    output, setOf(),
                    listOf(
                        LootTableProvider.SubProviderEntry(
                            { QuartzBlockLoot(lookup.join(), plans) },
                            LootContextParamSets.BLOCK
                        )
                    ),
                    lookup
                )
            )

            val blockTags = QuartzBlockTagProvider(output, lookup, modId, helper, plans)
            event.generator.addProvider(true, blockTags)

            event.generator.addProvider(
                true,
                QuartzItemTagProvider(output, lookup, blockTags.contentsGetter(), modId, helper, plans)
            )
        }

        for ((modId, registries) in ItemRegistry.instances.groupBy { it.modId }) {
            val plans = registries.flatMap { it.plans }

            event.generator.addProvider(
                true,
                QuartzItemModelProvider(output, modId, helper, plans)
            )

            event.generator.addProvider(
                true,
                QuartzItemPlanTagProvider(output, lookup, modId, helper, plans)
            )
        }

        for ((modId, registries) in SoundRegistry.instances.groupBy { it.modId }) {
            val defs = registries.flatMap { it.soundDefs }

            event.generator.addProvider(
                true,
                QuartzSoundDefinitionsProvider(output, modId, helper, defs)
            )
        }
    }
}
