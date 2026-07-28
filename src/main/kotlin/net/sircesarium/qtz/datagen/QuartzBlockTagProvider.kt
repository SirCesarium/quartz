package net.sircesarium.qtz.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.TagContext
import java.util.concurrent.CompletableFuture

class QuartzBlockTagProvider(
    output: PackOutput,
    lookup: CompletableFuture<HolderLookup.Provider>,
    modId: String,
    helper: ExistingFileHelper?,
    private val plans: List<DatagenPlan>,
) : BlockTagsProvider(output, lookup, modId, helper), TagContext {
    override fun addTags(lookup: HolderLookup.Provider) {
        plans.forEach { it.emitTags(this) }
    }

    override fun add(tag: TagKey<Block>, block: Block) {
        tag(tag).add(block)
    }

    override fun add(tag: TagKey<Item>, item: ItemLike) {}
}
