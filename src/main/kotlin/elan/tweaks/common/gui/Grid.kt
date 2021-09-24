package elan.tweaks.common.gui

interface Grid<ElementT> {
    
    operator fun contains(uiPoint: Vector): Boolean
    fun findAt(uiPoint: Vector): ElementT?
    fun asOriginSequence(): Sequence<Pair<Vector, ElementT>>
    
}
