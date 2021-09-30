package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.VectorXY

interface MouseOverUIComponent: UIComponent {
    fun onMouseOver(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext)
}
