package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.drawing.TooltipDrawer
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.ScribeTools
import net.minecraft.util.StatCollector

class InkNotificationUIComponent(
    private val research: ResearchPort,
    private val scribeTools: ScribeTools,
    private val uiCenter: VectorXY,
) : BackgroundUIComponent {

    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (research.missingNotes() || scribeTools.arePresentAndNotEmpty()) return
        drawNoInkTooltip(context)
    }

    private fun drawNoInkTooltip(context: UIContext) {
        val noInkFirstLine = StatCollector.translateToLocal("tile.researchtable.noink.0")
        val noInkSecondLine = StatCollector.translateToLocal("tile.researchtable.noink.1")
        val screenOrigin = context.toScreenOrigin(uiCenter)
        
        TooltipDrawer.drawCentered(
            context, listOf(noInkFirstLine, noInkSecondLine), center = screenOrigin, 11
        )
    }

}
