package net.sircesarium.qtz.api.datagen

sealed class BlockShape {
    data object CubeAll : BlockShape()
    data class CubeColumn(val endSuffix: String = "_top") : BlockShape()
}
