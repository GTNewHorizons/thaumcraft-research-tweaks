package elan.tweaks.common.gui.drawing

import elan.tweaks.common.ext.drawQuads
import net.minecraft.client.renderer.Tessellator

/**
 * Based on
 * https://github.com/TimeConqueror/LootGames/blob/64c1bfbae24a0c6c14ef883c4c8d558d3778ee22/src/main/java/ru/timeconqueror/timecore/api/util/client/DrawHelper.java#L6
 *
 */
object TextureDrawer {
    /**
     * Draws textured rectangle.
     *
     *
     * Term, used in parameters:
     * Parts are used to determine the actual size of `textureX, textureY, textureWidth, textureHeight` and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as `textureXPartCount` it wil be split into 4 pieces with width 16.
     * Then, if we set `textureX` to 1 and `textureWidth` to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x               start x-coordinate. (x of left-top corner)
     * @param y               start y-coordinate. (y of left-top corner)
     * @param width            Represents coordinate length along the axis X.
     * @param height           Represents coordinate length along the axis Y.
     * @param zLevel           z-coordinate.
     * @param textureX         index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY         index of start subtexture part on axis Y (y of left-top texture corner).
     * @param textureWidth     subtexture width in parts.
     * @param textureHeight    subtexture height in parts.
     * @param textureXPartCount in how many parts texture must be divided in both axis. Part description is mentioned above.
     * Usually equal to original texture width in pixels.
     * @param textureYPartCount in how many parts texture must be divided in both axis. Part description is mentioned above.
     * Usually equal to original texture height in pixels.
     */
    fun drawTexturedRectByParts(
        x: Int, y: Int, width: Int, height: Int, zLevel: Double,
        textureX: Int, textureY: Int, textureWidth: Int, textureHeight: Int,
        textureXPartCount: Int, textureYPartCount: Int = textureXPartCount,
    ) {
        val portionXFactor = 1.0 / textureXPartCount
        val portionYFactor = 1.0 / textureYPartCount
        drawTexturedRect(x, y, width, height, zLevel, textureX, textureY, textureWidth, textureHeight, portionXFactor, portionYFactor)
    }

    /**
     * Draws textured rectangle.
     *
     *
     * Term, used in parameters:
     * Parts are used to determine the actual size of `textureX, textureY, textureWidth, textureHeight` and coordinates relative to the entire texture.
     * The example:
     * Total texture has 64x64 resolution.
     * Let's consider axis X:
     * If we provide 4 as `textureXPartCount` it wil be split into 4 pieces with width 16 on X axis.
     * Then, if we set `textureX` to 1 and `textureWidth` to 3, it will mean, that we need a texture with start at (1 * 16) by X and with end at (1 + 3) * 16 by X.
     *
     * @param x                  start x-coordinate. (x of left-top corner)
     * @param y                  start y-coordinate. (y of left-top corner)
     * @param width               Represents coordinate length along the axis X.
     * @param height              Represents coordinate length along the axis Y.
     * @param zLevel              z-coordinate.
     * @param textureX            index of start subtexture part on axis X (x of left-top texture corner).
     * @param textureY            index of start subtexture part on axis Y (y of left-top texture corner).
     * @param textureWidth        subtexture width in parts.
     * @param textureHeight       subtexture height in parts.
     * @param textureXDivideFactor represents the value equal to 1 / parts. Part count determines in how many parts texture must be divided in both axis. Part description is mentioned above.
     * @param textureYDivideFactor represents the value equal to 1 / parts. Part count determines in how many parts texture must be divided in both axis. Part description is mentioned above.
     */
    private fun drawTexturedRect(
        x: Int, y: Int, width: Int, height: Int, zLevel: Double,
        textureX: Int, textureY: Int, textureWidth: Int, textureHeight: Int,
        textureXDivideFactor: Double, textureYDivideFactor: Double = textureXDivideFactor
    ) {
        Tessellator.instance.drawQuads {
            addVertexWithUV(
                x.toDouble(), y.toDouble(), zLevel,
                (textureX * textureXDivideFactor), (textureY * textureYDivideFactor)
            )
            addVertexWithUV(
                x.toDouble(), (y + height).toDouble(), zLevel,
                (textureX * textureXDivideFactor), ((textureY + textureHeight) * textureYDivideFactor)
            )
            addVertexWithUV(
                (x + width).toDouble(), (y + height).toDouble(), zLevel,
                ((textureX + textureWidth) * textureXDivideFactor), ((textureY + textureHeight) * textureYDivideFactor)
            )
            addVertexWithUV(
                (x + width).toDouble(), y.toDouble(), zLevel,
                ((textureX + textureWidth) * textureXDivideFactor), (textureY * textureYDivideFactor)
            )
        }
    }
}
