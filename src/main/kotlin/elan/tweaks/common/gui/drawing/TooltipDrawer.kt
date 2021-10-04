package elan.tweaks.common.gui.drawing

import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.drawing.TooltipDrawer.ColorsHexValues.BLACK
import elan.tweaks.common.gui.drawing.TooltipDrawer.ColorsHexValues.PURPLE
import elan.tweaks.common.gui.drawing.TooltipDrawer.Distances.BORDER_HEIGHT
import elan.tweaks.common.gui.drawing.TooltipDrawer.Distances.HEADER_SPACING
import elan.tweaks.common.gui.drawing.TooltipDrawer.Distances.LINE_HEIGHT
import elan.tweaks.common.gui.drawing.TooltipDrawer.TextColors.GRAY
import elan.tweaks.common.gui.geometry.Scale
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.VectorXY
import net.minecraft.client.gui.FontRenderer
import org.lwjgl.opengl.GL11
import thaumcraft.client.lib.UtilsFX

object TooltipDrawer {

    fun drawCentered(context: UIContext, lines: List<String>, center: VectorXY, subTipColorColorHex: String) {
        val textScale = lines.textScale(context)
        val xOffset = Vector2D(textScale.width / 2, y = textScale.height / 2)
        draw(context, lines, textScale, origin = center - xOffset, subTipColorColorHex)
    }

    fun draw(context: UIContext, lines: List<String>, origin: VectorXY, subTipColorColorHex: String) {
        draw(context, lines, lines.textScale(context), origin, subTipColorColorHex)
    }

    private fun List<String>.textScale(
        context: UIContext
    ) = Scale(
        width = maxOfPixelWidth(context),
        height = textHeight()
    )

    private fun List<String>.maxOfPixelWidth(context: UIContext) = map { line ->
        context.fontRenderer.getStringWidth(line)
    }.maxOrNull() ?: 0

    private fun List<String>.textHeight() =
        BORDER_HEIGHT + headerHeight() + bodyHeight() + BORDER_HEIGHT

    private fun List<String>.headerHeight() =
        if (size > 1) HEADER_SPACING + LINE_HEIGHT else LINE_HEIGHT

    private fun List<String>.bodyHeight() = 
        (size - 2) * LINE_HEIGHT

    fun draw(context: UIContext, lines: List<String>, textScale: Scale, origin: VectorXY, subTipColorColorHex: String) {
        if (lines.isEmpty()) return

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_DEPTH_TEST)

        drawBackground(origin, textScale)
        drawBorders(origin, textScale)

        drawHeader(lines.first(), subTipColorColorHex, origin, context.fontRenderer)
        drawBody(lines.drop(1), origin, context)

        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glPopMatrix()
    }

    private fun drawBody(
        lines: List<String>,
        origin: VectorXY,
        context: UIContext
    ) {
        lines.forEachIndexed { index, line ->
            val yOffset = HEADER_SPACING + LINE_HEIGHT * (index + 1)
            context.fontRenderer.drawStringWithShadow("§$GRAY$line", origin.x, origin.y + yOffset, -1)
        }
    }

    private fun drawHeader(
        line: String,
        colorHex: String,
        origin: VectorXY,
        renderer: FontRenderer
    ) {
        renderer.drawStringWithShadow("§$colorHex$line", origin.x, origin.y, -1)
    }

    private fun drawBorders(origin: VectorXY, scale: Scale) {
        val topColor = PURPLE
        val bottomColor = BLACK
        UtilsFX.drawGradientRect(origin.x - 3, origin.y - 3 + 1, origin.x - 3 + 1, origin.y + scale.height + 3 - 1, topColor, bottomColor)
        UtilsFX.drawGradientRect(
            origin.x + scale.width + 2,
            origin.y - 3 + 1,
            origin.x + scale.width + 3,
            origin.y + scale.height + 3 - 1,
            topColor,
            bottomColor
        )
        UtilsFX.drawGradientRect(origin.x - 3, origin.y - 3, origin.x + scale.width + 3, origin.y - 3 + 1, topColor, topColor)
        UtilsFX.drawGradientRect(
            origin.x - 3,
            origin.y + scale.height + 2,
            origin.x + scale.width + 3,
            origin.y + scale.height + 3,
            bottomColor,
            bottomColor
        )
    }

    private fun drawBackground(origin: VectorXY, scale: Scale) {
        UtilsFX.drawGradientRect(origin.x - 3, origin.y - 4, origin.x + scale.width + 3, origin.y - 3, BLACK, BLACK)
        UtilsFX.drawGradientRect(origin.x - 3, origin.y + scale.height + 3, origin.x + scale.width + 3, origin.y + scale.height + 4, BLACK, BLACK)
        UtilsFX.drawGradientRect(origin.x - 3, origin.y - 3, origin.x + scale.width + 3, origin.y + scale.height + 3, BLACK, BLACK)
        UtilsFX.drawGradientRect(origin.x - 4, origin.y - 3, origin.x - 3, origin.y + scale.height + 3, BLACK, BLACK)
        UtilsFX.drawGradientRect(origin.x + scale.width + 3, origin.y - 3, origin.x + scale.width + 4, origin.y + scale.height + 3, BLACK, BLACK)
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
