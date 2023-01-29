package elan.tweaks.common.ext

import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.dto.VectorXY
import kotlin.math.roundToInt
import kotlin.math.sqrt
import thaumcraft.common.lib.utils.HexUtils

object HexMath {
  fun toCenterVector(hex: HexUtils.Hex, hexSize: Int) = toCenterVector(hex.q, hex.r, hexSize)

  fun toCenterVector(q: Int, r: Int, hexSize: Int): VectorXY =
      Vector2D(
          x = (hexSize * (1.5 * q)).roundToInt(),
          y = (hexSize * sqrt(3.0) * (r + q / 2.0)).roundToInt())

  fun toOrigin(q: Int, r: Int, hexSize: Int): VectorXY =
      toOrigin(toCenterVector(q, r, hexSize), hexSize)

  fun toOrigin(centerVector: VectorXY, hexSize: Int): VectorXY = centerVector - hexSize + 1
}
