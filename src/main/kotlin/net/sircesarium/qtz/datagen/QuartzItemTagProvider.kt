package net.sircesarium.qtz.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.data.tags.TagsProvider
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.TagContext
import java.util.concurrent.CompletableFuture

class QuartzItemTagProvider(
    output: PackOutput,
    lookup: CompletableFuture<HolderLookup.Provider>,
    blockTags: CompletableFuture<TagsProvider.TagLookup<Block>>,
    modId: String,
    helper: ExistingFileHelper?,
    private val plans: List<DatagenPlan>,
) : ItemTagsProvider(output, lookup, blockTags, modId, helper), TagContext {
    override fun addTags(lookup: HolderLookup.Provider) {
        plans.forEach { it.emitTags(this) }
    }

    override fun add(tag: TagKey<Block>, block: Block) {}

    override fun add(tag: TagKey<Item>, item: ItemLike) {
        tag(tag).add(item.asItem())
    }
}
