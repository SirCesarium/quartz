package net.sircesarium.qtz.api.datagen

object DatagenRegistry {
    val itemModels = mutableListOf<Pair<String, String>>()
    val handheldModels = mutableListOf<Pair<String, String>>()
    val blockModels = mutableListOf<Triple<String, String, BlockShape>>()
}
