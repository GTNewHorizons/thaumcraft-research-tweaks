package elan.tweaks.common.gui.dto

import elan.tweaks.common.ext.HexMath
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

// TODO: consider moving everything to float/double instead of ints (all is convert to them in the
// end anyway...)
data class Rgba(
    val r: Float,
    val g: Float,
    val b: Float,
    val a: Float,
)

data class UV(val u: Int, val v: Int)

data class Scale(val width: Int, val height: Int) {
  companion object {
    fun cube(side: Int) = Scale(side, side)
  }
}

interface VectorXY {
  val x: Int
  val y: Int

  operator fun times(scalar: Double): VectorXY
  operator fun minus(scalar: Int): VectorXY
  operator fun plus(scalar: Int): VectorXY
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

data class Vector3D(override val x: Int, override val y: Int, val z: Double) : VectorXY {
  override fun times(scalar: Double): Vector3D =
      Vector3D(x = (x * scalar).roundToInt(), y = (y * scalar).roundToInt(), z = z * scalar)

  override fun minus(scalar: Int): Vector3D =
      Vector3D(x = x - scalar, y = y - scalar, z = z - scalar)

  override fun plus(scalar: Int): Vector3D =
      Vector3D(x = x + scalar, y = y + scalar, z = z + scalar)

  override operator fun plus(scale: Scale) = Vector2D(x = x + scale.width, y = y + scale.height)
  operator fun plus(other: Vector3D) = Vector3D(x = x + other.x, y = y + other.y, z = z + other.z)
  operator fun minus(other: Vector3D) = Vector3D(x = x - other.x, y = y - other.y, z = z - other.z)
  override operator fun plus(other: VectorXY) = copy(x = x + other.x, y = y + other.y)
  override operator fun minus(other: VectorXY) = copy(x = x - other.x, y = y - other.y)

  companion object {
    val ZERO = Vector3D(0, 0, 0.0)
  }
}

data class Vector2D(override val x: Int, override val y: Int) : VectorXY {

  override fun times(scalar: Double): VectorXY =
      Vector2D(x = (x * scalar).roundToInt(), y = (y * scalar).roundToInt())

  override fun minus(scalar: Int): Vector2D = Vector2D(x = x - scalar, y = y - scalar)
  override fun plus(scalar: Int): VectorXY = Vector2D(x = x + scalar, y = y + scalar)
  override operator fun plus(scale: Scale) = Vector2D(x = x + scale.width, y = y + scale.height)
  operator fun plus(other: Vector3D) = other.copy(x = x + other.x, y = y + other.y)
  operator fun minus(other: Vector3D) = other.copy(x = x - other.x, y = y - other.y)
  override operator fun plus(other: VectorXY) = Vector2D(x = x + other.x, y = y + other.y)
  override operator fun minus(other: VectorXY) = Vector2D(x = x - other.x, y = y - other.y)

  companion object {
    val ZERO = Vector2D(0, 0)
  }
}

data class Rectangle(val origin: VectorXY, val scale: Scale) {
  private val originScaled by lazy { origin + scale }

  operator fun contains(point: VectorXY) = origin < point && point < originScaled

  operator fun plus(offset: VectorXY) = Rectangle(origin + offset, scale)
}

data class HexVector(
    val q: Int,
    val r: Int,
) {

  val key: String = "$q:$r"

  fun toOrigin(hexSize: Int): VectorXY = HexMath.toOrigin(q, r, hexSize)

  companion object {
    // Pixel to hex is from https://www.redblobgames.com/grids/hexagons/#pixel-to-hex
    // Hex rounding is from https://www.redblobgames.com/grids/hexagons/#rounding
    fun roundedFrom(vector: VectorXY, hexSize: Int): HexVector {
      val (roundQ, roundR) = findRoundedCoordinates(vector, hexSize)

      return HexVector(roundQ, roundR)
    }

    private fun findRoundedCoordinates(vector: VectorXY, hexSize: Int): Pair<Int, Int> {
      val q = 0.6666666666666666 * vector.x / hexSize.toDouble()
      val r = 0.3333333333333333 * (sqrt(3.0) * -vector.y - vector.x) / hexSize.toDouble()
      val s = -q - r

      var roundQ = q.roundToInt()
      val roundR = r.roundToInt()
      var roundS = s.roundToInt()
      val deltaQ = abs(roundQ - q)
      val deltaR = abs(roundR - r)
      val deltaS = abs(roundS - s)
      if (deltaQ > deltaR && deltaQ > deltaS) {
        roundQ = -roundR - roundS
      } else if (deltaR <= deltaS) {
        roundS = -roundQ - roundR
      }

      return roundQ to roundS
    }
  }
}
