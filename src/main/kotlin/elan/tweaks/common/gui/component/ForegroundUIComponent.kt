package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.Scale
import elan.tweaks.common.gui.geometry.VectorXY

interface ForegroundUIComponent: UIComponent {
    fun onDrawForeground(uiMousePosition: VectorXY, scale: Scale, context: UIContext)
}
