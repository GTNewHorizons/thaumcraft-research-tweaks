package elan.tweaks.common.gui.fx

import cpw.mods.fml.client.FMLClientHandler
import elan.tweaks.common.ext.draw
import elan.tweaks.common.gui.dto.VectorXY
import kotlin.math.sin
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11

object LineParticle {
  fun draw(from: VectorXY, to: VectorXY) {
    val count = nextValueOfSlowlyGrowingFunction()

    val alpha = 0.3 + sin(count.toDouble()) * 0.3 + 0.3
    GL11.glPushMatrix()
    GL11.glLineWidth(3.0f)
    GL11.glDisable(GL11.GL_TEXTURE_2D)
    GL11.glBlendFunc(770, 1)
    Tessellator.instance.draw(3) {
      setColorRGBA_F(0.0f, 0.6f, 0.8f, alpha.toFloat())
      addVertex(from.x.toDouble(), from.y.toDouble(), 0.0)
      addVertex(to.x.toDouble(), to.y.toDouble(), 0.0)
    }
    GL11.glBlendFunc(770, 771)
    GL11.glDisable(32826)
    GL11.glEnable(GL11.GL_TEXTURE_2D)
    GL11.glPopMatrix()
  }

  /** To simulate particle beating and color changing */
  private fun nextValueOfSlowlyGrowingFunction() =
      FMLClientHandler.instance().client.thePlayer.ticksExisted
}
