package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import net.minecraft.util.StatCollector

class ScribeToolsNotificationUIComponent(
    private val research: ResearchProcessPort,
    private val uiCenter: VectorXY,
) : BackgroundUIComponent {

  override fun onDrawBackground(
      uiMousePosition: VectorXY,
      partialTicks: Float,
      context: UIContext
  ) {
    if (research.requiresInkToContinue && research.notesPresent()) drawNoInkTooltip(context)
  }

  private fun drawNoInkTooltip(context: UIContext) {
    val noInkFirstLine = StatCollector.translateToLocal("tile.researchtable.noink.0")
    val noInkSecondLine = StatCollector.translateToLocal("tile.researchtable.noink.1")

    context.drawTooltipCentered(uiCenterPosition = uiCenter, noInkFirstLine, noInkSecondLine)
  }
}
