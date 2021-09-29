package elan.tweaks.common.gui.fx

import cpw.mods.fml.client.FMLClientHandler
import elan.tweaks.common.ext.draw
import elan.tweaks.common.gui.geometry.VectorXY
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.MathHelper
import org.lwjgl.opengl.GL11

object LineParticle {
    fun draw(start: VectorXY, end: VectorXY) {
        val count = nextValueOfSlowlyGrowingFunction()
        val alpha = 0.3f + MathHelper.sin((count.toFloat().toDouble() + start.x).toFloat()) * 0.3f + 0.3f
        GL11.glPushMatrix()
        GL11.glLineWidth(3.0f)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(770, 1)
        Tessellator.instance.draw(3) {
            setColorRGBA_F(0.0f, 0.6f, 0.8f, alpha)
            addVertex(start.x.toDouble(), start.y.toDouble(), 0.0)
            addVertex(end.x.toDouble(), end.y.toDouble(), 0.0)
        }
        GL11.glBlendFunc(770, 771)
        GL11.glDisable(32826)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glPopMatrix()
    }

    /**
     * To simulate particle beating and color changing
     */
    private fun nextValueOfSlowlyGrowingFunction() =
        FMLClientHandler.instance().client.thePlayer.ticksExisted
}
