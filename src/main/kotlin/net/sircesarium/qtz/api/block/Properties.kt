package net.sircesarium.qtz.api.block

import net.minecraft.world.level.block.state.BlockBehaviour

fun BlockBehaviour.Properties.noCollision(): BlockBehaviour.Properties = noCollission() // fixing Mojang's typo cuz my eyes hurt reading "collission"
