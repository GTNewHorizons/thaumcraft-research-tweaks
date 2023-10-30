package elan.tweaks.common.gui.layout

import elan.tweaks.common.gui.dto.VectorXY

interface Layout<ElementT> {
  operator fun contains(uiPoint: VectorXY): Boolean
  operator fun get(uiPoint: VectorXY): ElementT?
  fun asOriginList(): List<Pair<VectorXY, ElementT>>
}
