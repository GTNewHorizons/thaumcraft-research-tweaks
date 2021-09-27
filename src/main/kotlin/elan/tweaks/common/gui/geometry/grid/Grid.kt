package elan.tweaks.common.gui.geometry.grid

import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.VectorXY

interface Grid<ElementT> {
    
    operator fun contains(uiPoint: VectorXY): Boolean
    operator fun get(uiPoint: VectorXY): ElementT?
    fun asOriginSequence(): Sequence<Pair<VectorXY, ElementT>>
    
}
