package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component

import elan.tweaks.common.gui.component.*
import elan.tweaks.common.gui.dto.Rectangle
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.peripheral.MouseButton
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectsTreePort
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
    private val tree: AspectsTreePort
) : BackgroundUIComponent, MouseOverUIComponent, ClickableUIComponent, TickingUIComponent {

  private val clickDelayTicks = 10f
  private var clickedCoolDown = 0f
  private val notOnCoolDown
    get() = !onCoolDown
  private val onCoolDown
    get() = clickedCoolDown > 0

  override fun onDrawBackground(
      uiMousePosition: VectorXY,
      partialTicks: Float,
      context: UIContext
  ) {
    if (researcher.hasDiscovered(Knowledge.ResearchDuplication)) drawButton(context)
  }

  private fun drawButton(context: UIContext) {
    if (research.readyToDuplicate() && notOnCoolDown)
        context.drawBlending(CopyButtonActiveTexture, bounds.origin)
    else context.drawBlending(CopyButtonNotActiveTexture, bounds.origin)
  }

  override fun onMouseClicked(uiMousePosition: VectorXY, button: MouseButton, context: UIContext) {
    if (uiMousePosition !in bounds || onCoolDown) return

    clickedCoolDown = clickDelayTicks

    duplicate(context)
  }

  private fun duplicate(context: UIContext) {
    research.duplicate().onSuccess { context.playButtonClicked() }
  }

  private fun UIContext.playButtonClicked() = apply {
    playSound(
        soundName = "thaumcraft:cameraclack", volume = 0.4f, pitch = 1.0f, distanceDelay = false)
  }

  override fun onTick(partialTicks: Float, context: UIContext) {
    if (clickedCoolDown <= 0) return
    clickedCoolDown -= partialTicks
  }

  override fun onMouseOver(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
    if (uiMousePosition in bounds && shouldShowRequirements())
        drawRequirements(research.usedAspectAmounts, context)
  }

  private fun shouldShowRequirements() =
      researcher.hasDiscovered(Knowledge.ResearchDuplication) && research.complete()

  private fun drawRequirements(usedAspectAmounts: Map<Aspect, Int>, context: UIContext) {

    val header = StatCollector.translateToLocal("tc.research.copy")

    GL11.glPushMatrix()
    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    context.drawWithShadow(header, requirementsUiOrigin)
    val textHeight = 10
    context.drawBlending(
        CopyRequirementsTexture, requirementsUiOrigin + Vector2D(x = 0, y = textHeight))
    GL11.glPopMatrix()

    usedAspectAmounts.entries
        .sortedBy { (aspect, _) -> tree.orderOf(aspect) }
        .forEachIndexed { index, (aspect, amount) ->
          val tagOffset =
              Vector2D(x = CopyRequirementsTexture.scale.width + index * 16, y = textHeight)
          context.drawTag(aspect, amount, requirementsUiOrigin + tagOffset)
        }
  }
}
