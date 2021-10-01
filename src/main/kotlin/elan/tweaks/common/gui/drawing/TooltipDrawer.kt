package elan.tweaks.common.gui.drawing

import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.geometry.Scale
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.VectorXY
import org.lwjgl.opengl.GL11
import thaumcraft.client.lib.UtilsFX

object TooltipDrawer {

    fun drawCentered(context: UIContext, lines: List<String>, center: VectorXY, subTipColor: Int) {
        val textScale = lines.textScale(context)
        val xOffset = Vector2D(textScale.width / 2, y = textScale.height / 2)
        draw(context, lines, textScale, origin = center - xOffset, subTipColor)
    }

    fun draw(context: UIContext, lines: List<String>, origin: VectorXY, subTipColor: Int) {
        draw(context, lines, lines.textScale(context), origin, subTipColor)
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
        8 + (size - 1) * 10 + if (size > 1) 2 else 0


    // TODO: Rewrite, fix rect shading
    fun draw(context: UIContext, lines: List<String>, textScale: Scale, origin: VectorXY, subTipColor: Int) {
        GL11.glPushMatrix()
        GL11.glDisable(32826) // GL12.GL_RESCALE_NORMAL ?
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        if (lines.isNotEmpty()) {
            val xOffset = origin.x
            var yOffset = origin.y

            context.setItemRenderZLevel(300.0f)
            val var10 = -267386864
            UtilsFX.drawGradientRect(xOffset - 3, yOffset - 4, xOffset + textScale.width + 3, yOffset - 3, var10, var10)
            UtilsFX.drawGradientRect(xOffset - 3, yOffset + textScale.height + 3, xOffset + textScale.width + 3, yOffset + textScale.height + 4, var10, var10)
            UtilsFX.drawGradientRect(xOffset - 3, yOffset - 3, xOffset + textScale.width + 3, yOffset + textScale.height + 3, var10, var10)
            UtilsFX.drawGradientRect(xOffset - 4, yOffset - 3, xOffset - 3, yOffset + textScale.height + 3, var10, var10)
            UtilsFX.drawGradientRect(xOffset + textScale.width + 3, yOffset - 3, xOffset + textScale.width + 4, yOffset + textScale.height + 3, var10, var10)
            val var11 = 1347420415
            val var12 = var11 and 16711422 shr 1 or var11 and -16777216
            UtilsFX.drawGradientRect(xOffset - 3, yOffset - 3 + 1, xOffset - 3 + 1, yOffset + textScale.height + 3 - 1, var11, var12)
            UtilsFX.drawGradientRect(
                xOffset + textScale.width + 2,
                yOffset - 3 + 1,
                xOffset + textScale.width + 3,
                yOffset + textScale.height + 3 - 1,
                var11,
                var12
            )
            UtilsFX.drawGradientRect(xOffset - 3, yOffset - 3, xOffset + textScale.width + 3, yOffset - 3 + 1, var11, var11)
            UtilsFX.drawGradientRect(xOffset - 3, yOffset + textScale.height + 2, xOffset + textScale.width + 3, yOffset + textScale.height + 3, var12, var12)
            for (var13 in lines.indices) {
                var var14 = lines[var13]
                var14 = if (var13 == 0) {
                    "§" + Integer.toHexString(subTipColor) + var14
                } else {
                    "§7$var14"
                }
                context.fontRenderer.drawStringWithShadow(var14, xOffset, yOffset, -1)
                if (var13 == 0) {
                    yOffset += 2
                }
                yOffset += 10
            }
        }
        context.setItemRenderZLevel(0.0f)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glPopMatrix()
    }

}
