package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.common.gui.TooltipUtil
import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.ClickableUIComponent
import elan.tweaks.common.gui.component.ScreenUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.geometry.grid.Grid
import net.minecraft.client.gui.inventory.GuiContainer.isShiftKeyDown
import org.lwjgl.opengl.GL11
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.lib.UtilsFX

class AspectPool(
    private val aspectGrid: Grid<Aspect>,
    private val findAspectAmount: (Aspect) -> Float,
    private val findBonusAspectAmount: (Aspect) -> Int,
    private val sendCombinationRequestToServer: (Aspect) -> Unit // TODO: hide behind domain logic
) : BackgroundUIComponent, ScreenUIComponent, ClickableUIComponent {

    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) =
        aspectGrid
            .asOriginSequence()
            .forEach { (uiOrigin, aspect) ->
                val amount = findAspectAmount(aspect)
                val bonusAmount = findBonusAspectAmount(aspect)

                // TODO: Hide this behind some aspect drawing component
                val faded = amount <= 0 && bonusAmount <= 0
                val alpha = if (faded) 0.33f else 1.0f

                val absolutePosition = context.toScreenOrigin(uiOrigin)
                UtilsFX.drawTag(
                    absolutePosition.x, absolutePosition.y,
                    aspect, amount, bonusAmount,
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
        TooltipUtil.drawCustomTooltip(
            context, listOf(aspect.name, aspect.localizedDescription), Vector2D(0, y = -8) + screenOrigin, 11
        )
        // TODO: Add research check, this should be somehow passed via Domain
        if (!aspect.isPrimal) {
            GL11.glPushMatrix()
            GL11.glEnable(3042)
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
            GL11.glDisable(3042)
            GL11.glPopMatrix()
        }
    }

    companion object {
        const val ASPECT_TAG_BLEND = 771
    }

    override fun onMouseClicked(uiMousePosition: VectorXY, button: Int, context: UIContext) {
        whenAspectAt(uiMousePosition) { aspect ->
            // TODO: Blow logic, including some called methods should be in domain or pass through. Also should check for RESEARCHER_2 (research mastery)
            if (isShiftKeyDown() && aspect.isCompound && aspect.bothComponentsPresent) {
                playCombine(context)
                sendCombinationRequestToServer(aspect)
            }
        }
    }

    private fun whenAspectAt(mousePosition: VectorXY, action: (Aspect) -> Unit) {
        aspectGrid[mousePosition]
            ?.run(action)
    }

    private val Aspect.bothComponentsPresent get() = componentPresent(0) && componentPresent(1)

    private fun Aspect.componentPresent(index: Int, minimumAmount: Int = 1) = isCompound
            && (findAspectAmount(components[index]) + findBonusAspectAmount(components[index]) >= minimumAmount)

    private val Aspect.isCompound get() = !isPrimal

    private fun playCombine(context: UIContext) =
        context.playSoundOnEntity(
            soundName = "thaumcraft:hhon",
            volume = 0.3f,
            pitch = 1.0f,
            distanceDelay = false
        )

}
