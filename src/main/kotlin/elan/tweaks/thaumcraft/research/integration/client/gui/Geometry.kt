package elan.tweaks.thaumcraft.research.integration.client.gui


data class Rectangle(
    val origin: Point,
    val scale: Scale
) {
    private val originScaled by lazy { origin + scale }

    fun contains(point: Point) =
        origin < point && point < originScaled
}

data class Scale(val width: UInt, val height: UInt)
data class Point(val x: UInt, val y: UInt) {
    operator fun plus(scale: Scale) = Point(x = x + scale.width, y = y + scale.height)
    operator fun compareTo(other: Point) =
        when {
            this.x.toInt() == other.x.toInt() && this.y.toInt() == other.y.toInt() -> EQUAL
            this.x.toInt() <= other.x.toInt() && this.y.toInt() <= other.y.toInt() -> THIS_LESS
            else -> THIS_GREATER
        }

    companion object {
        const val THIS_LESS = -1
        const val EQUAL = 0
        const val THIS_GREATER = 1
    }
}

