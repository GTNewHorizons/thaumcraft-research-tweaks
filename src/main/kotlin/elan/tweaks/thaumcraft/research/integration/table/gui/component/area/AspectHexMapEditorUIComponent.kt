package elan.tweaks.thaumcraft.research.integration.table.gui.component.area

import elan.tweaks.common.gui.component.ClickableUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.component.dragndrop.DragClickableDestinationUIComponent
import elan.tweaks.common.gui.component.dragndrop.DropDestinationUIComponent
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.common.gui.peripheral.MouseButton
import elan.tweaks.thaumcraft.research.domain.ports.provided.ResearchPort
import elan.tweaks.thaumcraft.research.integration.adapters.layout.AspectHex
import thaumcraft.api.aspects.Aspect

class AspectHexMapEditorUIComponent(
    private val research: ResearchPort,
    private val hexLayout: HexLayout<AspectHex>,
) : ClickableUIComponent,
    DragClickableDestinationUIComponent,
    DropDestinationUIComponent {

    override fun onMouseClicked(uiMousePosition: VectorXY, button: MouseButton, context: UIContext) {
        whenHexAt(uiMousePosition) { hex ->
            if(hex !is AspectHex.Occupied.Node) return // TODO: this check should be a part of domain

            research.erase(hex.key).onSuccess {
                context
                    .playCombine()
                    .playErase()
            }
        }
    }

    override fun onDropped(draggable: Any, uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (draggable !is Aspect) return

        writeToHexWhenPresent(uiMousePosition, draggable, context)
    }

    override fun onDragClick(draggable: Any, uiMousePosition: VectorXY, button: MouseButton, context: UIContext) {
        if (draggable !is Aspect) return
        if (button !is MouseButton.Right) return

        writeToHexWhenPresent(uiMousePosition, draggable, context)
    }

    private fun writeToHexWhenPresent(
        uiMousePosition: VectorXY,
        draggable: Aspect,
        context: UIContext
    ) {
        whenHexAt(uiMousePosition) { hex ->
            if (hex !is AspectHex.Vacant) return // TODO: this check should be a part of domain

            research.write(hex.key, draggable).onSuccess {
                context
                    .playCombine()
                    .playWrite()
            }
        }
    }

    private inline fun whenHexAt(uiMousePosition: VectorXY, action: (AspectHex) -> Unit) {
        if (research.notEditable()) return
        hexLayout[uiMousePosition]?.run(action)
    }

    private fun UIContext.playCombine() = apply {
        playSoundOnEntity(
            soundName = "thaumcraft:hhon",
            volume = 0.3f,
            pitch = 1.0f,
            distanceDelay = false
        )
    }

    private fun UIContext.playWrite() = apply {
        playSoundOnEntity(
            soundName = "thaumcraft:write",
            volume = 0.2f,
            pitch = 1.0f,
            distanceDelay = false
        )
    }

    private fun UIContext.playErase() = apply {
        playSoundOnEntity(
            soundName = "thaumcraft:erase",
            volume = 0.2f,
            pitch = 1.0f + nextRandomFloat() * 0.1f,
            distanceDelay = false
        )
    }

}
