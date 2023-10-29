package elan.tweaks.common.gui.component.dragndrop

import elan.tweaks.common.gui.component.UIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.VectorXY

interface DraggableSourceUIComponent : UIComponent {
  fun onDrag(uiMousePosition: VectorXY, context: UIContext): Any?
}
