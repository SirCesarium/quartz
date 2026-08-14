package net.sircesarium.qtz.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider
import net.sircesarium.qtz.api.sound.SoundDef

internal class QuartzSoundDefinitionsProvider(
    output: PackOutput,
    private val modId: String,
    helper: ExistingFileHelper,
    private val defs: List<SoundDef>,
) : SoundDefinitionsProvider(output, modId, helper) {
    override fun registerSounds() {
        for (def in defs) {
            val loc = ResourceLocation.fromNamespaceAndPath(modId, def.name)
            add(def.name, definition().with(sound(loc)))
        }
    }
}