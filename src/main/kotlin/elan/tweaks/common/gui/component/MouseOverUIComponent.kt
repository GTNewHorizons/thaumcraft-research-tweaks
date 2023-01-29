package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.dto.VectorXY

interface MouseOverUIComponent : UIComponent {
  fun onMouseOver(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext)
}
