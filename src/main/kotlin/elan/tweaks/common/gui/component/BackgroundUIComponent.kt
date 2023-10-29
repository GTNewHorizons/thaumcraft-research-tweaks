package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.dto.VectorXY

interface BackgroundUIComponent : UIComponent {
  fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext)
}
