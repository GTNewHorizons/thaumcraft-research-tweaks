package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component

import elan.tweaks.common.gui.component.MouseOverUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.Rectangle
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout.UsageHintLayout
import net.minecraft.util.StatCollector

class UsageHintUIComponent(
    private val bounds: Rectangle,
    private val hintPosition: VectorXY,
): MouseOverUIComponent {
    override fun onMouseOver(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (uiMousePosition in bounds)  drawNoInkTooltip(uiMousePosition, context)
    }

    private fun drawNoInkTooltip(uiMousePosition: VectorXY, context: UIContext) {
        val hintFirstLine = StatCollector.translateToLocal("researchtable.usagehint.header")
        val hintSecondLine = StatCollector.translateToLocal("researchtable.usagehint.description")
        context.drawTooltipVerticallyCentered(uiCenterPosition = uiMousePosition + UsageHintLayout.textBoxOffset, hintFirstLine, hintSecondLine)
    }
}
