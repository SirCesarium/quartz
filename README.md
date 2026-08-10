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
        ModTabs.register(modEventBus)
        QuartzDataGatherers.register(modEventBus)
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

## Creative Tabs

```kotlin
class ModTabs : TabRegistry("modid") {
    val myTab by tab(before = CreativeModeTabs.COMBAT) {
        title = Component.translatable("itemGroup.modid.my_tab")
        icon = ModItems.myItem

        +ModItems.myItem
        +ModBlocks.myBlock

        val mySection by section("My Section", rgb(26, 26, 46), rgb(187, 170, 102)) {
            +ModItems.myItem
            +itemTag
        }
    }
}
```

Register it in your `@Mod` class like any other registry.

The tab name comes from the property (snake_case). `title` defaults to `itemGroup.<modid>.<tab_name>`, `icon` defaults to the first item. Use `before`/`after` to position the tab.

Inside the tab you can add items with `+` (accepts an `ItemLike`, `TagKey<Item>` or a `BlockWithItem`). `section()` adds a Fancy Tab Sections banner; without FTS installed the tab degrades gracefully to a vanilla tab. Section name comes from the property (snake_case).

Add items to an existing vanilla tab with `addTo` (instead of a `BuildCreativeModeTabContentsEvent` listener):

```kotlin
val someBlockInBuilding by addTo(CreativeModeTabs.BUILDING_BLOCKS) {
    +ModBlocks.someBlock
    +itemTag
}
```

## Color helpers

```kotlin
rgb(26, 26, 46)              // opaque RGB → ARGB int
hsl(210f, 0.5f, 0.3f)        // HSL → ARGB int
hex("1a1a2e")                // hex, optional #
hex("#BBAA66")
```

## Datagen

Call `QuartzDataGatherers.register(modEventBus)` in your `@Mod` class.

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
