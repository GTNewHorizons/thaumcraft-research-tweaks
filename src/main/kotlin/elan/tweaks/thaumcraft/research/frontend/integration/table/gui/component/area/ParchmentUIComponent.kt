package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component.area

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.TickingUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.Rgba
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ParchmentTexture

class ParchmentUIComponent(
    private val research: ResearchProcessPort,
    private val runes: Runes,
    private val uiOrigin: VectorXY,
) : BackgroundUIComponent, TickingUIComponent {

  override fun onDrawBackground(
      uiMousePosition: VectorXY,
      partialTicks: Float,
      context: UIContext
  ) {
    if (research.missingNotes()) return

    drawParchment(context)
    drawRunes(context)
  }

  private fun drawParchment(context: UIContext) {
    context.drawBlending(ParchmentTexture, uiOrigin)
  }

  private fun drawRunes(context: UIContext) {
    runes.forEach { rune ->
      val colorMask = Rgba(0f, 0f, 0f, rune.alpha)
      context.drawBlending(rune.texture, rune.uiOrigin, colorMask)
    }
  }

  override fun onTick(partialTicks: Float, context: UIContext) {
    if (research.missingNotes()) {
      runes.clear()
    }
    runes.update(partialTicks, context.random)
  }
}
