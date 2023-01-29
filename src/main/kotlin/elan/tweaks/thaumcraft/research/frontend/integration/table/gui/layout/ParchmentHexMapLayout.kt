package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout

import elan.tweaks.common.ext.HexMath.toCenterVector
import elan.tweaks.common.ext.HexMath.toOrigin
import elan.tweaks.common.ext.allNeighbors
import elan.tweaks.common.ext.key
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.HexTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ParchmentTexture
import java.util.*
import thaumcraft.common.lib.utils.HexUtils

object ParchmentHexMapLayout {

  private val keyToHex: Map<String, Hex> = findAllFittingHexes()
  override fun toString(): String = "ParchmentHexMapLayout: $keyToHex"

  fun randomHex() = keyToHex.entries.random()

  // TODO: Consider doing this in O(n) time, where n is a number of hexes on the parchment by using
  // math
  private fun findAllFittingHexes(): Map<String, Hex> {
    val hexSize = HexTexture.SIZE_PIXELS

    val fittingHexes = mutableMapOf<String, Hex>()
    val visitedKeys = mutableSetOf<String>()
    val hexesToTraverse = Stack<HexUtils.Hex>()
    hexesToTraverse += HexUtils.Hex(0, 0)
    visitedKeys += "0:0"

    while (hexesToTraverse.isNotEmpty()) {
      val hex = hexesToTraverse.pop()

      val center = toCenterVector(hex.q, hex.r, hexSize) + ParchmentTexture.centerOrigin
      val origin = toOrigin(center, hexSize)
      if (center !in ParchmentTexture.drawableBounds) continue

      fittingHexes += hex.key to Hex(origin = origin, center = center)
      val neighbors = hex.allNeighbors.filterNot { neighbor -> neighbor.key in visitedKeys }
      hexesToTraverse += neighbors
      visitedKeys += neighbors.map { neighbor -> neighbor.key }
    }

    return fittingHexes - "0:0"
  }

  data class Hex(val origin: VectorXY, val center: VectorXY)
}
