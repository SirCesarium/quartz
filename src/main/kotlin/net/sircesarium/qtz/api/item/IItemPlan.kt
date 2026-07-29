package net.sircesarium.qtz.api.item

import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.sircesarium.qtz.api.TagContext

interface IItemPlan {
    val item: Item
    fun emitItemModel(provider: ItemModelProvider) {}
    fun emitTags(context: TagContext) {}
}
