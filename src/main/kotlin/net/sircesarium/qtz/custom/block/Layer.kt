package net.sircesarium.qtz.custom.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

open class LayerBlock(properties: Properties) : Block(properties) {
    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.LAYERS, 1))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.LAYERS)
    }

    override fun canBeReplaced(state: BlockState, context: BlockPlaceContext): Boolean {
        val layers = state.getValue(BlockStateProperties.LAYERS)
        if (context.itemInHand.`is`(asItem()) && layers < 8) {
            return if (context.replacingClickedOnBlock()) context.clickedFace === Direction.UP else true
        }
        return layers == 1
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val existingState = context.level.getBlockState(context.clickedPos)
        if (existingState.`is`(this)) {
            return existingState.setValue(BlockStateProperties.LAYERS, minOf(8, existingState.getValue(BlockStateProperties.LAYERS) + 1))
        }
        return super.getStateForPlacement(context) ?: defaultBlockState()
    }

    override fun useShapeForLightOcclusion(state: BlockState): Boolean = true

    override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float {
        return if (state.getValue(BlockStateProperties.LAYERS) == 8) 0.2f else 1.0f
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPES_BY_LAYER[state.getValue(BlockStateProperties.LAYERS)]
    }

    override fun getCollisionShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPES_BY_LAYER[state.getValue(BlockStateProperties.LAYERS) - 1]
    }

    override fun getBlockSupportShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape {
        return SHAPES_BY_LAYER[state.getValue(BlockStateProperties.LAYERS)]
    }

    override fun getVisualShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPES_BY_LAYER[state.getValue(BlockStateProperties.LAYERS)]
    }

    override fun getOcclusionShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape {
        return SHAPES_BY_LAYER[state.getValue(BlockStateProperties.LAYERS)]
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val belowState = level.getBlockState(pos.below())
        return isFaceFull(belowState.getCollisionShape(level, pos.below()), Direction.UP)
            || (belowState.`is`(this) && belowState.getValue(BlockStateProperties.LAYERS) == 8)
    }

    override fun updateShape(state: BlockState, direction: Direction, neighborState: BlockState, level: LevelAccessor, pos: BlockPos, neighborPos: BlockPos): BlockState {
        return if (!state.canSurvive(level, pos)) Blocks.AIR.defaultBlockState()
        else super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun isRandomlyTicking(state: BlockState): Boolean = false

    companion object {
        val SHAPES_BY_LAYER = arrayOf(
            Shapes.empty(),
            box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
        )
    }
}

open class WaterloggableLayerBlock(properties: Properties) : LayerBlock(properties), SimpleWaterloggedBlock {
    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(BlockStateProperties.WATERLOGGED)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val waterlogged = context.level.getFluidState(context.clickedPos).type === Fluids.WATER
        return super.getStateForPlacement(context).setValue(BlockStateProperties.WATERLOGGED, waterlogged)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(BlockStateProperties.WATERLOGGED)) Fluids.WATER.source.defaultFluidState()
        else super.getFluidState(state)
    }

    override fun updateShape(state: BlockState, direction: Direction, neighborState: BlockState, level: LevelAccessor, pos: BlockPos, neighborPos: BlockPos): BlockState {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }
}
