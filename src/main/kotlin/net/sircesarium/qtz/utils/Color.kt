package net.sircesarium.qtz.utils

import kotlin.math.abs

fun rgb(r: Int, g: Int, b: Int): Int = (0xFF shl 24) or (r shl 16) or (g shl 8) or b

fun rgba(r: Int, g: Int, b: Int, a: Int): Int = (a shl 24) or (r shl 16) or (g shl 8) or b

fun hsl(h: Float, s: Float, l: Float): Int {
    val chroma = (1 - abs(2 * l - 1)) * s
    val hue = ((h % 360) + 360) % 360
    val x = chroma * (1 - abs((hue / 60) % 2 - 1))
    val m = l - chroma / 2

    val (r, g, b) = when (hue.toInt()) {
        in 0 until 60 -> Triple(chroma, x, 0f)
        in 60 until 120 -> Triple(x, chroma, 0f)
        in 120 until 180 -> Triple(0f, chroma, x)
        in 180 until 240 -> Triple(0f, x, chroma)
        in 240 until 300 -> Triple(x, 0f, chroma)
        else -> Triple(chroma, 0f, x)
    }

    return rgb(
        ((r + m) * 255).toInt().coerceIn(0, 255),
        ((g + m) * 255).toInt().coerceIn(0, 255),
        ((b + m) * 255).toInt().coerceIn(0, 255),
    )
}

fun hex(hex: String): Int {
    val cleaned = hex.removePrefix("#")
    val value = cleaned.toLong(16)
    return if (cleaned.length == 6) (0xFF shl 24) or value.toInt() else value.toInt()
}
