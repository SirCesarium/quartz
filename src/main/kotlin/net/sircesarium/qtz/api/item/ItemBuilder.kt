package net.sircesarium.qtz.api.item

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

open class ItemScope {
    var model: Boolean = true
    var customTexture: ResourceLocation? = null

    @PublishedApi internal var propertiesCustomizer: (Item.Properties.() -> Unit)? = null

    fun configureProperties(action: Item.Properties.() -> Unit) {
        propertiesCustomizer = action
    }

    fun texture(path: String) {
        customTexture = ResourceLocation.parse(path)
    }
}

class ItemBuilder<I : Item> : ItemScope() {
    @PublishedApi internal var itemFactory: ((Item.Properties) -> I)? = null

    fun customItem(factory: (Item.Properties) -> I) {
        itemFactory = factory
    }
}
