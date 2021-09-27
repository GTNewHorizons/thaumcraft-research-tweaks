package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.VectorXY

interface BackgroundUIComponent: UIComponent {
    fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext)
}
