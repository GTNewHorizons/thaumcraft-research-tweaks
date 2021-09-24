package elan.tweaks.common.gui

data class UV(val u: Int, val v: Int)
data class Scale(val width: Int, val height: Int)
data class Vector(val x: Int, val y: Int) {

    operator fun plus(scale: Scale) = Vector(x = x + scale.width, y = y + scale.height)
    operator fun plus(other: Vector) = Vector(x = x + other.x, y = y + other.y)
    operator fun minus(other: Vector) = Vector(x = x - other.x, y = y - other.y)
    
    operator fun compareTo(other: Vector) =
        when {
            this.x == other.x && this.y == other.y -> EQUAL
            this.x <= other.x && this.y <= other.y -> THIS_LESS
            else -> THIS_GREATER
        }

    companion object {
        const val THIS_LESS = -1
        const val EQUAL = 0
        const val THIS_GREATER = 1

        val ZERO = Vector(0, 0)
    }
}
data class Rectangle(
    val origin: Vector,
    val scale: Scale
) {
    private val originScaled by lazy { origin + scale }

    operator fun contains(point: Vector) =
        origin < point && point < originScaled
    
    operator fun plus(offset: Vector) = Rectangle(origin + offset, scale)
    
}
