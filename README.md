# Quartz

A Kotlin DSL for NeoForge modding. Declarative block/item registration with auto-BlockItem and built-in datagen.

## Quick Start

```kotlin
class ModBlocks : BlockRegistry("modid") {
    val myBlock by block {
        configureBlock { strength(2f) }
    }

    val myStair by stair(Blocks.STONE) {
        texture("minecraft:block/polished_andesite")
    }

    val mySlab by slab {
        configureBlock { strength(1.5f) }
    }
}

class ModItems : ItemRegistry("modid") {
    val myItem by item {
        configureProperties { stacksTo(16) }
        texture("modid:item/custom_thing")
    }

    val mySword by sword(Tiers.IRON) {
        attackDamage = 4f
        attackSpeed = -2.8f
    }
}

// In your @Mod class:
class MyMod(modEventBus: IEventBus) {
    init {
        ModBlocks.register(modEventBus)
        ModItems.register(modEventBus)
        // Datagen is handled automatically by Quartz
    }
}
```

## Block Types

| Type | Function | Params | Auto-generates |
|---|---|---|---|
| **Basic** | `block {}` | `configureBlock {}`, `configureItem {}` | block item |
| **Custom class** | `block<MyBlock> {}` | `customBlock(::MyBlock)`, `configureBlock {}` | block item |
| **Stair** | `stair(baseBlock?) {}` | `baseBlock`, `texture()`, `renderType`, `configureBlock {}` | stair models (inner/outer/straight), blockstate, loot |
| **Slab** | `slab(baseBlock?) {}` | `baseBlock`, `texture()`, `renderType`, `configureBlock {}` | slab model, blockstate, loot |
| **Wall** | `wall(baseBlock?) {}` | `baseBlock`, `texture()`, `renderType`, `configureBlock {}` | wall models (post/side), blockstate, loot |
| **Fence** | `fence(baseBlock?) {}` | `baseBlock`, `texture()`, `renderType`, `configureBlock {}` | fence models (post/side), blockstate, loot |
| **Fence Gate** | `fenceGate(baseBlock?, woodType) {}` | `baseBlock`, `woodType`, `texture()`, `configureBlock {}` | gate models, blockstate, loot, tags |
| **Button** | `button(baseBlock?, blockSetType, ticks) {}` | `baseBlock`, `blockSetType`, `ticks`, `texture()`, `configureBlock {}` | button model, blockstate, loot |
| **Pressure Plate** | `pressurePlate(baseBlock?, blockSetType) {}` | `baseBlock`, `blockSetType`, `texture()`, `configureBlock {}` | plate model, blockstate, loot |
| **Pillar** | `pillar(baseBlock?) {}` | `baseBlock`, `textureSide()`, `textureEnd()`, `configureBlock {}` | column models (axis variants), blockstate, loot |
| **Door** | `door(baseBlock?) {}` | `baseBlock`, `textureBottom()`, `textureTop()`, `configureBlock {}` | door models (bottom/top), blockstate, loot |
| **Trapdoor** | `trapdoor(baseBlock?) {}` | `baseBlock`, `texture()`, `renderType`, `configureBlock {}` | trapdoor models (open/closed/top), blockstate, loot |
| **Flower** | `flower(baseBlock?) {}` | `baseBlock`, `texture()`, `configureBlock {}` | flower model, blockstate, loot |
| **Tall Flower** | `tallFlower(baseBlock?) {}` | `baseBlock`, `textureBottom()`, `textureTop()`, `configureBlock {}` | tall flower models (bottom/top), blockstate, loot |
| **Torch** | `torch(baseBlock?) {}` | `baseBlock`, `texture()`, `configureBlock {}` | torch + wall torch models, blockstate, loot |
| **Snow Layer** | `snowLayer(baseBlock?) {}` | `baseBlock`, `texture()`, `configureBlock {}` | layer models (heights 1–8), blockstate, loot |

When `baseBlock` is provided, the texture defaults to the base block's registry ID (`namespace:block/path`).
When omitted, the texture auto-resolves from the property name by stripping the type suffix (e.g. `my_stairs` → `block/my`, `oak_fence_gate` → `block/oak`).

## Item Types

| Type | Function | Params | Auto-generates |
|---|---|---|---|
| **Basic** | `item {}` | `configureProperties {}`, `texture()`, `model` | item model |
| **Custom class** | `item<MyItem> {}` | `customItem(::MyItem)`, `configureProperties {}`, `texture()` | item model |
| **Sword** | `sword(tier) {}` | `tier`, `attackDamage`, `attackSpeed`, `configureProperties {}`, `texture()` | handheld model, sword/sword_enchantable/weapon/damage tags |
| **Pickaxe** | `pickaxe(tier) {}` | `tier`, `attackDamage`, `attackSpeed`, `configureProperties {}`, `texture()` | handheld model, pickaxe/pickaxe_enchantable/mining tags |
| **Axe** | `axe(tier) {}` | `tier`, `attackDamage`, `attackSpeed`, `configureProperties {}`, `texture()` | handheld model, axe/axe_enchantable/mining tags |
| **Shovel** | `shovel(tier) {}` | `tier`, `attackDamage`, `attackSpeed`, `configureProperties {}`, `texture()` | handheld model, shovel/shovel_enchantable tags |
| **Hoe** | `hoe(tier) {}` | `tier`, `attackDamage`, `attackSpeed`, `configureProperties {}`, `texture()` | handheld model, hoe/hoe_enchantable/mining tags |

Textures default to `modid:item/<snake_case_name>`. Override with `texture("modid:path")`.

## Datagen

Datagen hooks up automatically — Quartz registers its `GatherDataEvent` listener
in the built-in `@Mod` class. No setup needed.

Each block type auto-generates:
- Blockstate JSON
- Block models
- Item model
- Loot table (self-drop by default, configurable)

Each item type auto-generates:
- Item model (`item/generated` or `item/handheld` for tools)
- Tag entries (tools only)

# License

MIT. See [LICENSE](./LICENSE) for details.
