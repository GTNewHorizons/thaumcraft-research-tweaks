package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.MouseOverUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.Rectangle
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort.Knowledge
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout.UsageHintLayout
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.UsageHintResearchExpertiseTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.UsageHintTexture
import net.minecraft.util.StatCollector

class UsageHintUIComponent(
    private val uiBounds: Rectangle,
    private val onMouseOverBounds: Rectangle,
    private val researcher: ResearcherKnowledgePort
) : BackgroundUIComponent, MouseOverUIComponent {

  override fun onDrawBackground(
      uiMousePosition: VectorXY,
      partialTicks: Float,
      context: UIContext
  ) {
    if (researcher.hasDiscovered(Knowledge.ResearchExpertise)) {
      context.drawBlending(UsageHintResearchExpertiseTexture, uiBounds.origin)
    } else {
      context.drawBlending(UsageHintTexture, uiBounds.origin)
    }
  }

  override fun onMouseOver(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
    if (uiMousePosition in onMouseOverBounds) drawHint(uiMousePosition, context)
  }

  private fun drawHint(uiMousePosition: VectorXY, context: UIContext) {
    val hintHeaderLine = StatCollector.translateToLocal("researchtable.usagehint.header")
    val hintDescriptionLine = StatCollector.translateToLocal("researchtable.usagehint.description")

    if (researcher.hasDiscovered(Knowledge.ResearchExpertise)) {
      val hintResearchExpertise =
          StatCollector.translateToLocal("researchtable.usagehint.research_expertise")
      context.drawTooltipVerticallyCentered(
          uiCenterPosition = uiMousePosition + UsageHintLayout.textBoxOffset,
          hintHeaderLine,
          hintDescriptionLine,
          hintResearchExpertise)
    } else {
      context.drawTooltipVerticallyCentered(
          uiCenterPosition = uiMousePosition + UsageHintLayout.textBoxOffset,
          hintHeaderLine,
          hintDescriptionLine)
    }
  }
}
