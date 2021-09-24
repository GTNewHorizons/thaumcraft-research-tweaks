package elan.tweaks.common.gui

interface Grid<ElementT> {
    
    operator fun contains(uiPoint: Vector): Boolean
    operator fun get(uiPoint: Vector): ElementT?
    fun asOriginSequence(): Sequence<Pair<Vector, ElementT>>
    
}
