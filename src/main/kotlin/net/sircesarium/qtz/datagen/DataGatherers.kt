package net.sircesarium.qtz.datagen

import net.minecraft.data.PackOutput
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.api.item.ItemRegistry

object DataGatherers {
    fun gatherData(event: GatherDataEvent) {
        val unscopedOutput = PackOutput(event.generator.packOutput.outputFolder.parent)
        val lookupProvider = event.lookupProvider

        val modIds = (BlockRegistry.instances.map { it.modId } + ItemRegistry.instances.map { it.modId }).toSet()

        for (modId in modIds) {
            val itemRegs = ItemRegistry.instances.filter { it.modId == modId }
            val blockRegs = BlockRegistry.instances.filter { it.modId == modId }

            event.generator.addProvider(
                true,
                QuartzItemModelProvider(
                    unscopedOutput, modId, event.existingFileHelper,
                    itemRegs.flatMap { it.itemModels },
                    itemRegs.flatMap { it.handheldModels },
                )
            )

            if (blockRegs.isNotEmpty()) {
                event.generator.addProvider(
                    true,
                    QuartzBlockStateProvider(
                        unscopedOutput, modId, event.existingFileHelper,
                        blockRegs.flatMap { it.blockModels },
                        blockRegs.flatMap { it.slabBlocks },
                        blockRegs.flatMap { it.stairBlocks },
                        blockRegs.flatMap { it.snowLayerBlocks },
                    )
                )

                event.generator.addProvider(
                    true, LootTableProvider(
                        unscopedOutput, setOf(),
                        listOf(
                            LootTableProvider.SubProviderEntry(
                                {
                                    QuartzBlockLootProvider(
                                        event.lookupProvider.join(), modId,
                                        blockRegs.flatMap { it.blockModels },
                                        blockRegs.flatMap { it.slabBlocks },
                                        blockRegs.flatMap { it.stairBlocks },
                                        blockRegs.flatMap { it.snowLayerBlocks },
                                    )
                                },
                                LootContextParamSets.BLOCK
                            )
                        ),
                        event.lookupProvider
                    )
                )
            }
        }
    }
}
