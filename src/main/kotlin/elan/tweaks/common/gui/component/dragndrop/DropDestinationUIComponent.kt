package elan.tweaks.common.gui.component.dragndrop

import elan.tweaks.common.gui.component.UIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.geometry.VectorXY

interface DropDestinationUIComponent : UIComponent {

    fun onDropped(draggable: Any, uiMousePosition: VectorXY, partialTicks: Float, context: UIContext)

}
