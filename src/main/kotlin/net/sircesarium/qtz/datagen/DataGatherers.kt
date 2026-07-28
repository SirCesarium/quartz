package net.sircesarium.qtz.datagen

import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.item.ItemRegistry

object DataGatherers {
    fun gatherData(event: GatherDataEvent) {
        val output = event.generator.packOutput
        val lookup = event.lookupProvider
        val helper = event.existingFileHelper

        for (reg in BlockRegistry.instances) {
            val plans = reg.plans

            event.generator.addProvider(
                true,
                QuartzBlockStateProvider(output, reg.modId, helper, plans)
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

            val blockTags = QuartzBlockTagProvider(output, lookup, reg.modId, helper, plans)
            event.generator.addProvider(true, blockTags)

            event.generator.addProvider(
                true,
                QuartzItemTagProvider(output, lookup, blockTags.contentsGetter(), reg.modId, helper, plans)
            )
        }

        for (reg in ItemRegistry.instances) {
            event.generator.addProvider(
                true,
                QuartzItemModelProvider(output, reg.modId, helper, reg.plans)
            )
        }
    }
}
