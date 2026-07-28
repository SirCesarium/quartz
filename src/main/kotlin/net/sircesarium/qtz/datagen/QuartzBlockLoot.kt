package net.sircesarium.qtz.datagen

import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.sircesarium.qtz.api.DatagenPlan
import net.sircesarium.qtz.api.LootTableContext

class QuartzBlockLoot(
    lookup: HolderLookup.Provider,
    private val plans: List<DatagenPlan>,
) : BlockLootSubProvider(setOf<Item>(), FeatureFlags.VANILLA_SET, lookup), LootTableContext {
    override fun getKnownBlocks(): Iterable<Block> = plans.flatMap { it.knownBlocks }

    override fun dropSelf(block: Block) {
        add(block, createSingleItemTable(block))
    }

    override fun dropSlab(block: Block) {
        add(block, createSlabItemTable(block))
    }

    override fun dropLayered(block: Block, layersProperty: IntegerProperty, perLayer: Int) {
        add(block, createLayeredDropTable(block, layersProperty, perLayer))
    }

    override fun dropOther(block: Block, dropsAs: ItemLike) {
        add(block, createSingleItemTable(dropsAs))
    }

    override fun addLootTable(block: Block, builder: LootTable.Builder) {
        add(block, builder)
    }

    private fun createLayeredDropTable(block: Block, property: IntegerProperty, perLayer: Int): LootTable.Builder {
        val pool = LootPool.lootPool()
        for (value in property.possibleValues) {
            pool.add(
                LootItem.lootTableItem(block)
                    .`when`(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value))
                    )
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly((value * perLayer).toFloat())))
            )
        }
        return LootTable.lootTable().withPool(pool)
    }

    override fun generate() {
        plans.forEach { plan ->
            plan.emitLootTable(this)
        }
    }
}
