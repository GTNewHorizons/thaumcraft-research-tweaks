package elan.tweaks.common.ext

import thaumcraft.common.lib.utils.HexUtils

val HexUtils.Hex.key
  get() = toString()
val HexUtils.Hex.allNeighbors
  get() = (0..5).map(::getNeighbour)
