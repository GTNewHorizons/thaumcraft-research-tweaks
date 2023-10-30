package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.peripheral.MouseButton

interface ClickableUIComponent : UIComponent {
  fun onMouseClicked(uiMousePosition: VectorXY, button: MouseButton, context: UIContext)
}
