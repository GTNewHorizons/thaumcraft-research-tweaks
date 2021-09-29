package elan.tweaks.thaumcraft.research.integration.table.gui.component

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.thaumcraft.research.domain.ports.api.ResearchAreaPort
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ParchmentTexture

class ResearchAreaUIComponent(
    private val area: ResearchAreaPort,
    private val uiOrigin: VectorXY
) : BackgroundUIComponent {
    
    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if(area.missingResearchNotes()) return

        drawBackgroundParchment(context)

    }

    private fun drawBackgroundParchment(context: UIContext) {
        ParchmentTexture.draw(
            origin = context.toScreenOrigin(uiOrigin),
            disableStandardLightning = true
        )
    }

}
