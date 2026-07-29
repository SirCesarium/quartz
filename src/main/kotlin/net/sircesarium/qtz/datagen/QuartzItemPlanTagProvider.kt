package net.sircesarium.qtz.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import net.minecraft.world.item.Item
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.sircesarium.qtz.api.TagContext
import net.sircesarium.qtz.api.item.IItemPlan
import net.minecraft.world.level.block.Block
import net.minecraft.tags.TagKey
import net.minecraft.world.level.ItemLike
import java.util.concurrent.CompletableFuture

class QuartzItemPlanTagProvider(
    output: PackOutput,
    lookup: CompletableFuture<HolderLookup.Provider>,
    modId: String,
    helper: ExistingFileHelper?,
    private val plans: List<IItemPlan>,
) : IntrinsicHolderTagsProvider<Item>(output, Registries.ITEM, lookup, { BuiltInRegistries.ITEM.getResourceKey(it).orElseThrow() }, modId, helper), TagContext {
    override fun addTags(lookup: HolderLookup.Provider) {
        plans.forEach { it.emitTags(this) }
    }

    override fun add(tag: TagKey<Block>, block: Block) {}

    override fun add(tag: TagKey<Item>, item: ItemLike) {
        this.tag(tag).add(item.asItem())
    }
}
