package elan.tweaks.common.gui.drawing

import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.geometry.VectorXY
import org.lwjgl.opengl.GL11
import thaumcraft.client.lib.UtilsFX

object TooltipDrawer {
    // TODO: Rewrite, fix rect shading
    fun drawCustomTooltip(context: UIContext, lines: List<*>, mousePosition: VectorXY, subTipColor: Int) {
        GL11.glDisable(32826)
        GL11.glDisable(2929)
        if (lines.isNotEmpty()) {
            var var5 = 0
            val var6 = lines.iterator()
            var var16: Int
            while (var6.hasNext()) {
                val var7 = var6.next() as String
                var16 = context.fontRenderer.getStringWidth(var7)
                if (var16 > var5) {
                    var5 = var16
                }
            }
            val var15 = mousePosition.x + 12
            var16 = mousePosition.y - 12
            var var9 = 8
            if (lines.size > 1) {
                var9 += 2 + (lines.size - 1) * 10
            }
            context.setItemRenderZLevel(300.0f)
            val var10 = -267386864
            UtilsFX.drawGradientRect(var15 - 3, var16 - 4, var15 + var5 + 3, var16 - 3, var10, var10)
            UtilsFX.drawGradientRect(var15 - 3, var16 + var9 + 3, var15 + var5 + 3, var16 + var9 + 4, var10, var10)
            UtilsFX.drawGradientRect(var15 - 3, var16 - 3, var15 + var5 + 3, var16 + var9 + 3, var10, var10)
            UtilsFX.drawGradientRect(var15 - 4, var16 - 3, var15 - 3, var16 + var9 + 3, var10, var10)
            UtilsFX.drawGradientRect(var15 + var5 + 3, var16 - 3, var15 + var5 + 4, var16 + var9 + 3, var10, var10)
            val var11 = 1347420415
            val var12 = var11 and 16711422 shr 1 or var11 and -16777216
            UtilsFX.drawGradientRect(var15 - 3, var16 - 3 + 1, var15 - 3 + 1, var16 + var9 + 3 - 1, var11, var12)
            UtilsFX.drawGradientRect(var15 + var5 + 2, var16 - 3 + 1, var15 + var5 + 3, var16 + var9 + 3 - 1, var11, var12)
            UtilsFX.drawGradientRect(var15 - 3, var16 - 3, var15 + var5 + 3, var16 - 3 + 1, var11, var11)
            UtilsFX.drawGradientRect(var15 - 3, var16 + var9 + 2, var15 + var5 + 3, var16 + var9 + 3, var12, var12)
            for (var13 in lines.indices) {
                var var14 = lines[var13] as String
                var14 = if (var13 == 0) {
                    "§" + Integer.toHexString(subTipColor) + var14
                } else {
                    "§7$var14"
                }
                context.fontRenderer.drawStringWithShadow(var14, var15, var16, -1)
                if (var13 == 0) {
                    var16 += 2
                }
                var16 += 10
            }
        }
        context.setItemRenderZLevel(0.0f)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }
}
