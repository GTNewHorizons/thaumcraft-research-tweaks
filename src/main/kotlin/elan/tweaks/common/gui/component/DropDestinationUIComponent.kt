package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.VectorXY

interface DropDestinationUIComponent : UIComponent {

    fun onDropped(draggable: Any, uiMousePosition: VectorXY, partialTicks: Float, context: UIContext)

}
