package net.sircesarium.qtz.api.util

fun String.toSnakeCase() = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
