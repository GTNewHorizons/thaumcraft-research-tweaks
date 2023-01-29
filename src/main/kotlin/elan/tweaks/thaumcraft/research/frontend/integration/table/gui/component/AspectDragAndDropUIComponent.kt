package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component

import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.component.dragndrop.DragAndDropUIComponent
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectPalletPort
import thaumcraft.api.aspects.Aspect

class AspectDragAndDropUIComponent(private val pallet: AspectPalletPort) : DragAndDropUIComponent {
  private var draggable: Aspect? = null

  override fun onAttemptDrag(draggable: Any, uiMousePosition: VectorXY, context: UIContext) {
    if (this.draggable != null || draggable !is Aspect) return

    this.draggable = draggable
  }

  override fun onDropping(context: UIContext): Any? {
    val droppedAspect = draggable ?: return null

    draggable = null
    return droppedAspect
  }

  override fun onDragClick(context: UIContext): Any? = draggable

  override fun onDragging(uiMousePosition: VectorXY, context: UIContext) {
    val draggedAspect = draggable ?: return
    if (pallet.isDrainedOf(draggedAspect)) {
      draggable = null
      return
    }

    context.drawOrb(uiMousePosition, draggedAspect.color)
  }
}
