package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.Vector3D
import elan.tweaks.common.gui.geometry.VectorXY

interface ScreenUIComponent: UIComponent {
    fun onDrawScreen(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext)
}
