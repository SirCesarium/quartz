package net.sircesarium.qtz.api.block

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

class BlockBuilder<B : Block> {
    @PublishedApi internal var itemCustomizer: (Item.Properties.() -> Unit)? = null
    @PublishedApi internal var blockCustomizer: (BlockBehaviour.Properties.() -> Unit)? = null
    @PublishedApi internal var blockFactory: ((BlockBehaviour.Properties) -> B)? = null

    fun configureItem(action: Item.Properties.() -> Unit) {
        this.itemCustomizer = action
    }

    fun configureBlock(action: BlockBehaviour.Properties.() -> Unit) {
        this.blockCustomizer = action
    }

    fun customBlock(factory: (BlockBehaviour.Properties) -> B) {
        this.blockFactory = factory
    }
}
