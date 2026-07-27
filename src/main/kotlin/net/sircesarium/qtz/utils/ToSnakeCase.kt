package net.sircesarium.qtz.utils

fun String.toSnakeCase(): String {
    return this
        .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1_$2")
        .replace(Regex("([a-z0-9])([A-Z])"), "$1_$2")
        .lowercase()
}