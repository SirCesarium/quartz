package net.sircesarium.qtz.api.tab

import net.mcexpanded.fancytabsections.FancyTabSections
import net.mcexpanded.fancytabsections.Section.SectionColored
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

internal object FTSAdapter {
    fun apply(modId: String, defs: List<TabDef>) {
        for (def in defs) {
            val tabLoc = ResourceLocation.fromNamespaceAndPath(modId, def.name)
            for (section in def.sections) {
                val sectionLoc = ResourceLocation.fromNamespaceAndPath(modId, section.name)
                val sectionColored = SectionColored(sectionLoc)
                    .setTitle(Component.literal(section.display))
                    .setBannerColor(section.banner)
                    .setTextColor(section.text)
                    .setTextShadow(true)

                for (item in section.items) {
                    when (item) {
                        is TabItem.Entry -> sectionColored.add(item.item)
                        is TabItem.Tag -> sectionColored.addItemTag(item.tag)
                    }
                }

                FancyTabSections.addSection(tabLoc, sectionColored)
            }
        }
    }
}
