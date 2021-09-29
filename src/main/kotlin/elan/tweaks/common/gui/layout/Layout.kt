package elan.tweaks.common.gui.layout

import elan.tweaks.common.gui.geometry.VectorXY

interface Layout<ElementT> {
    operator fun contains(uiPoint: VectorXY): Boolean
    operator fun get(uiPoint: VectorXY): ElementT?
    fun asOriginSequence(): Sequence<Pair<VectorXY, ElementT>>
}
