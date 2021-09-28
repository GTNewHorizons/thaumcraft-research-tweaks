package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.peripheral.MouseButton

interface ClickableUIComponent: UIComponent {
    fun onMouseClicked(uiMousePosition: VectorXY, button: MouseButton, context: UIContext)
}
