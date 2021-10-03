package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component

import elan.tweaks.common.gui.component.*
import elan.tweaks.common.gui.drawing.AspectDrawer
import elan.tweaks.common.gui.geometry.Rectangle
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.peripheral.MouseButton
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort.Knowledge
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.CopyButtonActiveTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.CopyButtonNotActiveTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.CopyRequirementsTexture
import net.minecraft.util.StatCollector
import org.lwjgl.opengl.GL11
import thaumcraft.api.aspects.Aspect

class CopyButtonUIComponent(
    private val bounds: Rectangle,
    private val requirementsUiOrigin: VectorXY,
    private val research: ResearchProcessPort,
    private val researcher: ResearcherKnowledgePort,
) : BackgroundUIComponent, MouseOverUIComponent, ClickableUIComponent, TickingUIComponent {

    private val clickDelayTicks = 10f
    private var clickedCoolDown = 0f
    private val notOnCoolDown get() = !onCoolDown
    private val onCoolDown get() = clickedCoolDown > 0

    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (researcher.hasDiscovered(Knowledge.ResearchDuplication)) drawButton(context)
    }

    private fun drawButton(context: UIContext) {
        val screenOrigin = context.toScreenOrigin(bounds.origin)

        if (research.readyToDuplicate() && notOnCoolDown) CopyButtonActiveTexture.draw(screenOrigin)
        else CopyButtonNotActiveTexture.draw(screenOrigin)
    }

    override fun onMouseClicked(uiMousePosition: VectorXY, button: MouseButton, context: UIContext) {
        if (uiMousePosition !in bounds || onCoolDown) return

        clickedCoolDown = clickDelayTicks

        duplicate(context)
    }

    private fun duplicate(context: UIContext) {
        research
            .duplicate()
            .onSuccess {
                context.playButtonClicked()
            }
    }

    private fun UIContext.playButtonClicked() = apply {
        playSoundOnEntity(
            soundName = "thaumcraft:cameraclack",
            volume = 0.4f,
            pitch = 1.0f,
            distanceDelay = false
        )
    }

    override fun onTick(partialTicks: Float, context: UIContext) {
        if (clickedCoolDown <= 0) return
        clickedCoolDown -= partialTicks
    }

    override fun onMouseOver(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (uiMousePosition in bounds && shouldShowRequirements()) drawRequirements(research.usedAspectAmounts, context)
    }

    private fun shouldShowRequirements() =
        researcher.hasDiscovered(Knowledge.ResearchDuplication) && research.readyToDuplicate()

    private fun drawRequirements(usedAspectAmounts: Map<Aspect, Int>, context: UIContext) {
        val screenOrigin = context.toScreenOrigin(requirementsUiOrigin)

        val header = StatCollector.translateToLocal("tc.research.copy")

        GL11.glPushMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        context.fontRenderer.drawStringWithShadow(header, screenOrigin.x, screenOrigin.y, -1)
        val textHeight = 10
        CopyRequirementsTexture.draw(screenOrigin + Vector2D(x = 0, y  = textHeight))
        GL11.glPopMatrix()

        usedAspectAmounts
            // TODO: add order
            .entries
            .forEachIndexed { index, (aspect, amount) ->
                val tagOffset = Vector2D(
                    x = CopyRequirementsTexture.width + index * 16,
                    y = textHeight
                )
                AspectDrawer.drawTag(screenOrigin + tagOffset, aspect, amount.toFloat())
            }
        
    }

}
