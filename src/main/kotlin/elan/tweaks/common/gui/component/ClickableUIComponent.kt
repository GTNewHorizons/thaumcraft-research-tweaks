package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.Vector3D
import elan.tweaks.common.gui.geometry.VectorXY

interface ClickableUIComponent: UIComponent {
    fun onMouseClicked(uiMousePosition: VectorXY, button: Int, context: UIContext)
}
