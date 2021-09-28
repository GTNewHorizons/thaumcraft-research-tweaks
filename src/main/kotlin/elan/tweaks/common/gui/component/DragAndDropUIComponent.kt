package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.VectorXY

interface DragAndDropUIComponent: UIComponent {
    
    /**
     * Called when left mouse button down and any source can provide a draggable
     */
    fun onAttemptDrag(draggables: List<Any>, uiMousePosition: VectorXY, context: UIContext) {
        draggables.forEach { draggable ->
            onAttemptDrag(draggable, uiMousePosition, context)
        }
    }
    
    /**
     * Called when left mouse button down and any source can provide a draggable
     */
    fun onAttemptDrag(draggable: Any, uiMousePosition: VectorXY, context: UIContext)
    
    /**
     * Called when left mouse button is down
     */
    fun onDragging(uiMousePosition: VectorXY, context: UIContext)

    /**
     * Called repeatedly when left mouse button is up
     */
    fun onDropping(context: UIContext): Any?
    
}
