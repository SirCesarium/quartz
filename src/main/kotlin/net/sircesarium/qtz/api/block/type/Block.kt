package net.sircesarium.qtz.api.block.type

import net.sircesarium.qtz.api.block.BlockRegistry
import net.sircesarium.qtz.utils.toSnakeCase

fun BlockRegistry.block() = bindName { name ->
    val block = blocks.registerSimpleBlock(name.toSnakeCase())

    // Block item
    items.registerSimpleBlockItem(name.toSnakeCase(), block)

    block
}