package elan.tweaks.common.gui.component.dragndrop

import elan.tweaks.common.gui.component.UIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.peripheral.MouseButton

interface DragClickableDestinationUIComponent : UIComponent {

  fun onDragClick(
      draggable: Any,
      uiMousePosition: VectorXY,
      button: MouseButton,
      context: UIContext
  )
}
