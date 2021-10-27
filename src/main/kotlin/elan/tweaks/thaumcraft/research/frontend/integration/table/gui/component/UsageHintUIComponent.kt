package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.MouseOverUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.Rectangle
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout.UsageHintLayout
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.UsageHintTexture
import net.minecraft.util.StatCollector

class UsageHintUIComponent(
    private val uiBounds: Rectangle,
    private val onMouseOverBounds: Rectangle,
): BackgroundUIComponent, MouseOverUIComponent {

    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext){
        context.drawBlending(UsageHintTexture, uiBounds.origin)
    }

    override fun onMouseOver(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (uiMousePosition in onMouseOverBounds) drawHint(uiMousePosition, context)
    }

    private fun drawHint(uiMousePosition: VectorXY, context: UIContext) {
        val hintFirstLine = StatCollector.translateToLocal("researchtable.usagehint.header")
        val hintSecondLine = StatCollector.translateToLocal("researchtable.usagehint.description")
        context.drawTooltipVerticallyCentered(uiCenterPosition = uiMousePosition + UsageHintLayout.textBoxOffset, hintFirstLine, hintSecondLine)
    }
}
