package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.VectorXY

interface ForegroundUIComponent : UIComponent {
  fun onDrawForeground(uiMousePosition: VectorXY, scale: Scale, context: UIContext)
}
