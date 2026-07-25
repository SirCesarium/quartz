# Quartz

A Kotlin DSL for NeoForge modding. Declarative block/item registration with auto‑BlockItem.

```kotlin
class MyBlocks : BlockRegistry("my_mod") {
    // Quartz infers the block ID automatically, e.g. "my_mod:petrified_wood"
    val petrifiedWood by block { }
    // val petrifiedWood by block("my_custom_name") { }

    val customClassBlock by block(::CustomClassBlock) { strength(50f) }

    // Quartz generates block items by default
    // You can disable it by passing QtzBlock(withItem = false)
    val noItemBlock by block(opts = QtzBlock(withItem = false)) { }
}

class MyItems(blocks: MyBlocks) : ItemRegistry("my_mod") {
    // Create your custom block item for `noItemBlock`
    val stoneItem by blockItem(blocks.noItemBlock) {
        stacksTo(1)
    }

    val myEpicItem by item { }
}
```

# License

MIT. See [LICENSE](./LICENSE) for details.
