package elan.tweaks.common.gui.geometry

data class UV(val u: Int, val v: Int)
data class Scale(val width: Int, val height: Int)

interface VectorXY {
    val x: Int
    val y: Int

    operator fun plus(scale: Scale): VectorXY
    operator fun plus(other: VectorXY): VectorXY
    operator fun minus(other: VectorXY): VectorXY

    operator fun compareTo(other: VectorXY) =
        when {
            this.x == other.x && this.y == other.y -> EQUAL
            this.x <= other.x && this.y <= other.y -> THIS_LESS
            else -> THIS_GREATER
        }

    companion object {
        const val THIS_LESS = -1
        const val EQUAL = 0
        const val THIS_GREATER = 1
    }
}

data class Vector3D(
    override val x: Int,
    override val y: Int,
    val z: Double
) : VectorXY {
    override operator fun plus(scale: Scale) = Vector2D(x = x + scale.width, y = y + scale.height)
    operator fun plus(other: Vector3D) = Vector3D(x = x + other.x, y = y + other.y, z = z + other.z)
    operator fun minus(other: Vector3D) = Vector3D(x = x - other.x, y = y - other.y, z = z - other.z)
    override operator fun plus(other: VectorXY) = copy(x = x + other.x, y = y + other.y)
    override operator fun minus(other: VectorXY) = copy(x = x - other.x, y = y - other.y)

    companion object {
        val ZERO = Vector3D(0, 0, 0.0)
    }
}

data class Vector2D(
    override val x: Int,
    override val y: Int
) : VectorXY {
    override operator fun plus(scale: Scale) = Vector2D(x = x + scale.width, y = y + scale.height)
    operator fun plus(other: Vector3D) = other.copy(x = x + other.x, y = y + other.y)
    operator fun minus(other: Vector3D) = other.copy(x = x - other.x, y = y - other.y)
    override operator fun plus(other: VectorXY) = Vector2D(x = x + other.x, y = y + other.y)
    override operator fun minus(other: VectorXY) = Vector2D(x = x - other.x, y = y - other.y)

    companion object {
        val ZERO = Vector2D(0, 0)
    }
}

data class Rectangle(
    val origin: VectorXY,
    val scale: Scale
) {
    private val originScaled by lazy { origin + scale }

    operator fun contains(point: VectorXY) =
        origin < point && point < originScaled

    operator fun plus(offset: VectorXY) = Rectangle(origin + offset, scale)

}
