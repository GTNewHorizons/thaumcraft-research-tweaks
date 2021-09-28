package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.VectorXY

interface DraggableSourceUIComponent: UIComponent {
    fun onDrag(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext): Any?
}
