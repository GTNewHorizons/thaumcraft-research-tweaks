package elan.tweaks.common.gui.rendering

import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.rendering.TooltipRenderer.ColorsHexValues.BLACK
import elan.tweaks.common.gui.rendering.TooltipRenderer.ColorsHexValues.PURPLE
import elan.tweaks.common.gui.rendering.TooltipRenderer.Distances.BORDER_HEIGHT
import elan.tweaks.common.gui.rendering.TooltipRenderer.Distances.HEADER_SPACING
import elan.tweaks.common.gui.rendering.TooltipRenderer.Distances.LINE_HEIGHT
import elan.tweaks.common.gui.rendering.TooltipRenderer.TextColors.GRAY
import net.minecraft.client.gui.FontRenderer
import org.lwjgl.opengl.GL11
import thaumcraft.client.lib.UtilsFX

object TooltipRenderer {

  fun drawCentered(
      lines: Array<out String>,
      center: VectorXY,
      subTipColorColorHex: String,
      fontRenderer: FontRenderer
  ) {
    val textScale = lines.textScale(fontRenderer)
    val offset = Vector2D(textScale.width / 2, y = textScale.height / 2)
    fontRenderer.draw(lines, textScale, origin = center - offset, subTipColorColorHex)
  }

  fun drawVerticallyCentered(
      lines: Array<out String>,
      center: VectorXY,
      subTipColorColorHex: String,
      fontRenderer: FontRenderer
  ) {
    val textScale = lines.textScale(fontRenderer)
    val offset = Vector2D(x = 0, y = textScale.height / 2)
    fontRenderer.draw(lines, textScale, origin = center - offset, subTipColorColorHex)
  }

  fun draw(
      lines: Array<out String>,
      origin: VectorXY,
      subTipColorColorHex: String,
      fontRenderer: FontRenderer
  ) {
    fontRenderer.draw(lines, lines.textScale(fontRenderer), origin, subTipColorColorHex)
  }

  private fun Array<out String>.textScale(fontRenderer: FontRenderer) =
      Scale(width = maxOfPixelWidth(fontRenderer), height = textHeight())

  private fun Array<out String>.maxOfPixelWidth(fontRenderer: FontRenderer) =
      map { line -> fontRenderer.getStringWidth(line) }.maxOrNull() ?: 0

  private fun Array<out String>.textHeight() =
      BORDER_HEIGHT + headerHeight() + bodyHeight() + BORDER_HEIGHT

  private fun Array<out String>.headerHeight() =
      if (size > 1) HEADER_SPACING + LINE_HEIGHT else LINE_HEIGHT

  private fun Array<out String>.bodyHeight() = (size - 2) * LINE_HEIGHT

  fun FontRenderer.draw(
      lines: Array<out String>,
      textScale: Scale,
      origin: VectorXY,
      subTipColorColorHex: String
  ) {
    if (lines.isEmpty()) return

    GL11.glPushMatrix()
    GL11.glDisable(GL11.GL_DEPTH_TEST)

    drawBackground(origin, textScale)
    drawBorders(origin, textScale)

    drawHeader(lines.first(), subTipColorColorHex, origin)
    drawBody(lines.drop(1), origin)

    GL11.glEnable(GL11.GL_DEPTH_TEST)
    GL11.glPopMatrix()
  }

  private fun FontRenderer.drawBody(lines: List<String>, origin: VectorXY) {
    lines.forEachIndexed { index, line ->
      val yOffset = HEADER_SPACING + LINE_HEIGHT * (index + 1)
      drawStringWithShadow("§$GRAY$line", origin.x, origin.y + yOffset, -1)
    }
  }

  private fun FontRenderer.drawHeader(line: String, colorHex: String, origin: VectorXY) {
    drawStringWithShadow("§$colorHex$line", origin.x, origin.y, -1)
  }

  private fun drawBorders(origin: VectorXY, scale: Scale) {
    val topColor = PURPLE
    val bottomColor = BLACK
    UtilsFX.drawGradientRect(
        origin.x - 3,
        origin.y - 3 + 1,
        origin.x - 3 + 1,
        origin.y + scale.height + 3 - 1,
        topColor,
        bottomColor)
    UtilsFX.drawGradientRect(
        origin.x + scale.width + 2,
        origin.y - 3 + 1,
        origin.x + scale.width + 3,
        origin.y + scale.height + 3 - 1,
        topColor,
        bottomColor)
    UtilsFX.drawGradientRect(
        origin.x - 3,
        origin.y - 3,
        origin.x + scale.width + 3,
        origin.y - 3 + 1,
        topColor,
        topColor)
    UtilsFX.drawGradientRect(
        origin.x - 3,
        origin.y + scale.height + 2,
        origin.x + scale.width + 3,
        origin.y + scale.height + 3,
        bottomColor,
        bottomColor)
  }

  private fun drawBackground(origin: VectorXY, scale: Scale) {
    UtilsFX.drawGradientRect(
        origin.x - 3, origin.y - 4, origin.x + scale.width + 3, origin.y - 3, BLACK, BLACK)
    UtilsFX.drawGradientRect(
        origin.x - 3,
        origin.y + scale.height + 3,
        origin.x + scale.width + 3,
        origin.y + scale.height + 4,
        BLACK,
        BLACK)
    UtilsFX.drawGradientRect(
        origin.x - 3,
        origin.y - 3,
        origin.x + scale.width + 3,
        origin.y + scale.height + 3,
        BLACK,
        BLACK)
    UtilsFX.drawGradientRect(
        origin.x - 4, origin.y - 3, origin.x - 3, origin.y + scale.height + 3, BLACK, BLACK)
    UtilsFX.drawGradientRect(
        origin.x + scale.width + 3,
        origin.y - 3,
        origin.x + scale.width + 4,
        origin.y + scale.height + 3,
        BLACK,
        BLACK)
  }

  object ColorsHexValues {
    const val BLACK = -0xFEFFFF0
    const val PURPLE = 0x505000FF
  }

  object TextColors {
    val LIGHT_BLUE: String = Integer.toHexString(11)
    val GRAY: String = Integer.toHexString(7)
  }

  object Distances {
    const val LINE_HEIGHT = 10
    const val HEADER_SPACING = 2
    const val BORDER_HEIGHT = 4
  }
}
