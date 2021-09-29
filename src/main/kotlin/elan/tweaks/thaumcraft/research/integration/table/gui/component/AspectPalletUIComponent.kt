package elan.tweaks.thaumcraft.research.integration.table.gui.component

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.ClickableUIComponent
import elan.tweaks.common.gui.component.ScreenUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.component.dragndrop.DragClickableDestinationUIComponent
import elan.tweaks.common.gui.component.dragndrop.DraggableSourceUIComponent
import elan.tweaks.common.gui.component.dragndrop.DropDestinationUIComponent
import elan.tweaks.common.gui.drawing.TooltipDrawer
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.geometry.grid.Grid
import elan.tweaks.common.gui.peripheral.MouseButton
import elan.tweaks.thaumcraft.research.domain.ports.api.AspectPalletPort
import net.minecraft.client.gui.inventory.GuiContainer.isCtrlKeyDown
import net.minecraft.client.gui.inventory.GuiContainer.isShiftKeyDown
import org.lwjgl.opengl.GL11
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.lib.UtilsFX

class AspectPalletUIComponent(
    private val aspectGrid: Grid<Aspect>,
    private val pallet: AspectPalletPort
) : BackgroundUIComponent, ScreenUIComponent, ClickableUIComponent,
    DraggableSourceUIComponent, DropDestinationUIComponent, DragClickableDestinationUIComponent {

    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) =
        aspectGrid
            .asOriginSequence()
            .forEach { (uiOrigin, aspect) ->
                val (amount, bonusAmount) = pallet.amountAndBonusOf(aspect)

                // TODO: Hide this behind some aspect drawing component
                val faded = pallet.isDrainedOf(aspect)
                val alpha = if (faded) 0.33f else 1.0f

                val absolutePosition = context.toScreenOrigin(uiOrigin)
                UtilsFX.drawTag(
                    absolutePosition.x, absolutePosition.y,
                    aspect, amount.toFloat(), bonusAmount,
                    absolutePosition.z,
                    ASPECT_TAG_BLEND, alpha
                )
            }

    override fun onDrawScreen(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) =
        whenAspectAt(uiMousePosition) { aspect ->
            drawTooltip(aspect, uiOrigin = uiMousePosition, context)
        }

    // TODO: Move to texture rendering object
    private fun drawTooltip(aspect: Aspect, uiOrigin: VectorXY, context: UIContext) {
        val screenOrigin = context.toScreenOrigin(uiOrigin)
        TooltipDrawer.drawCustomTooltip(
            context, listOf(aspect.name, aspect.localizedDescription), Vector2D(0, y = -8) + screenOrigin, 11
        )
        // TODO: Add research check, this should be somehow passed via Domain
        if (!aspect.isPrimal) {
            GL11.glPushMatrix()
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(770, 771)
            UtilsFX.bindTexture("textures/aspects/_back.png")
            GL11.glPushMatrix()
            GL11.glTranslated((screenOrigin.x + 6).toDouble(), (screenOrigin.y + 6).toDouble(), 0.0)
            GL11.glScaled(1.25, 1.25, 0.0)
            UtilsFX.drawTexturedQuadFull(0, 0, 0.0)
            GL11.glPopMatrix()
            GL11.glPushMatrix()
            GL11.glTranslated((screenOrigin.x + 24).toDouble(), (screenOrigin.y + 6).toDouble(), 0.0)
            GL11.glScaled(1.25, 1.25, 0.0)
            UtilsFX.drawTexturedQuadFull(0, 0, 0.0)
            GL11.glPopMatrix()
            UtilsFX.drawTag(screenOrigin.x + 26, screenOrigin.y + 8, aspect.components[1], 0.0f, 0, 0.0)
            UtilsFX.drawTag(screenOrigin.x + 8, screenOrigin.y + 8, aspect.components[0], 0.0f, 0, 0.0)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glPopMatrix()
        }
    }

    companion object {
        const val ASPECT_TAG_BLEND = 771
    }

    override fun onMouseClicked(uiMousePosition: VectorXY, button: MouseButton, context: UIContext) {
        whenAspectAt(uiMousePosition) { aspect ->
            if (button is MouseButton.Left && isIntendingToDeriveAspect()) {
                derive(aspect).onSuccess { context.playCombine() }
            }
        }
    }

    private fun derive(aspect: Aspect) =
        if (isIntendingToBatch()) pallet.deriveBatch(aspect)
        else pallet.derive(aspect)

    override fun onDrag(uiMousePosition: VectorXY, context: UIContext): Any? {
        val aspect = aspectGrid[uiMousePosition] ?: return null
        if (pallet.isDrainedOf(aspect) || isIntendingToDeriveAspect()) return null

        context.playButtonAspect()
        return aspect
    }

    private fun isIntendingToDeriveAspect() = 
        isShiftKeyDown()

    private fun UIContext.playButtonAspect() {
        playSoundOnEntity(
            "thaumcraft:hhoff",
            0.2f,
            1.0f + nextRandomFloat() * 0.1f,
            false
        )
    }
    
    override fun onDropped(draggable: Any, uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (draggable !is Aspect) return

        whenAspectAt(uiMousePosition) { targetAspect ->
            combine(draggable, targetAspect).onSuccess { context.playCombine() }
        }
    }


    override fun onDragClick(draggable: Any, uiMousePosition: VectorXY, button: MouseButton, context: UIContext) {
        if (draggable !is Aspect || button !is MouseButton.Right) return
        
        
        whenAspectAt(uiMousePosition) { targetAspect ->
            combine(draggable, targetAspect).onSuccess { context.playCombine() }
        }
    }

    private fun combine(draggable: Aspect, targetAspect: Aspect) =
        if (isIntendingToBatch()) pallet.combineBatch(draggable, targetAspect)
        else pallet.combine(draggable, targetAspect)

    private fun isIntendingToBatch() = 
        isCtrlKeyDown()

    private inline fun whenAspectAt(uiMousePosition: VectorXY, action: (Aspect) -> Unit) {
        aspectGrid[uiMousePosition]?.run(action)
    }

    private fun UIContext.playCombine() = apply {
        playSoundOnEntity(
            soundName = "thaumcraft:hhon",
            volume = 0.3f,
            pitch = 1.0f,
            distanceDelay = false
        )
    }

}
