package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component.area

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.dto.AspectHex
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.HexHighlightTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.HexTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.UnknownAspectTagTexture
import net.minecraft.util.StatCollector

class AspectHexMapUIComponent(
    private val centerUiOrigin: VectorXY,
    private val research: ResearchProcessPort,
    private val hexLayout: HexLayout<AspectHex>,
) : BackgroundUIComponent {

  override fun onDrawBackground(
      uiMousePosition: VectorXY,
      partialTicks: Float,
      context: UIContext
  ) {
    if (research.missingNotes()) return
    if (research.notesCorrupted()) return drawCorruptedNotes(context)

    // with current highlight texture best effect is achieved when drawing it under border
    drawMouseOverHighlight(uiMousePosition, context)
    drawHexes(context)
  }

  private fun drawCorruptedNotes(context: UIContext) {
    val header = StatCollector.translateToLocal("tc.research.notes.corrupted.line.1")
    val body = StatCollector.translateToLocal("tc.research.notes.corrupted.line.2")
    context.drawTooltipCentered(centerUiOrigin, header, body)
  }

  private fun drawMouseOverHighlight(uiMousePosition: VectorXY, context: UIContext) {
    if (research.notEditable()) return
    val hex = hexLayout[uiMousePosition] ?: return
    if (hex is AspectHex.Occupied.Root) return

    context.drawBlending(HexHighlightTexture, hex.uiOrigin, HexHighlightTexture.colorMask)
  }

  private fun drawHexes(context: UIContext) {
    hexLayout
        .asOriginList()
        .onEach { (uiOrigin, hex) ->
          if (hex !is AspectHex.Occupied.Root && research.incomplete())
              context.drawBlending(HexTexture, uiOrigin, HexTexture.colorMask)
        }
        .map(Pair<VectorXY, AspectHex>::second)
        .filterIsInstance<AspectHex.Occupied>()
        .onEach { hex -> drawConnections(hex, context) }
        .forEach { hex ->
          when (hex) {
            is AspectHex.Occupied.Root -> draw(hex, context)
            is AspectHex.Occupied.Node -> draw(hex, context)
          }
        }
  }

  private fun draw(hex: AspectHex.Occupied.Root, context: UIContext) {
    context.drawOrb(hex.uiCenter)
    if (research.shouldObfuscate(hex.aspect))
        context.drawBlending(
            UnknownAspectTagTexture, hex.uiOrigin, UnknownAspectTagTexture.colorMask)
    else context.drawTag(hex.aspect, uiPosition = hex.uiOrigin)
  }

  private fun draw(hex: AspectHex.Occupied.Node, context: UIContext) {
    when {
      research.shouldObfuscate(hex.aspect) ->
          context.drawBlending(
              UnknownAspectTagTexture, hex.uiOrigin, UnknownAspectTagTexture.colorMask)
      hex.notOnRootPath -> context.drawTagMonochrome(hex.aspect, uiPosition = hex.uiOrigin)
      else -> context.drawTag(hex.aspect, uiPosition = hex.uiOrigin)
    }
  }

  private fun drawConnections(hex: AspectHex.Occupied, context: UIContext) {
    hex.connectionTargetsCenters.forEach { connectionTargetCenter ->
      context.drawLine(hex.uiCenter, connectionTargetCenter)
    }
  }
}
