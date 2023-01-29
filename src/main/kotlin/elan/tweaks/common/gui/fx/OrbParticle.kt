package elan.tweaks.common.gui.fx

import cpw.mods.fml.client.FMLClientHandler
import elan.tweaks.common.ext.drawQuads
import elan.tweaks.common.gui.dto.Vector3D
import java.awt.Color
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.MathHelper
import org.lwjgl.opengl.GL11
import thaumcraft.client.fx.ParticleEngine
import thaumcraft.client.lib.UtilsFX
import thaumcraft.common.config.Config

object OrbParticle {
  fun draw(screenOrigin: Vector3D) {
    val seconds = nextValueOfSlowlyGrowingFunction()
    val red =
        0.7f +
            MathHelper.sin(((seconds.toFloat().toDouble() + screenOrigin.x) / 10.0).toFloat()) *
                0.15f +
            0.15f
    val green =
        0.7f +
            MathHelper.sin(
                ((seconds.toFloat().toDouble() + screenOrigin.x + screenOrigin.y) / 11.0)
                    .toFloat()) * 0.15f +
            0.15f
    val blue =
        0.7f +
            MathHelper.sin(((seconds.toFloat().toDouble() + screenOrigin.y) / 12.0).toFloat()) *
                0.15f +
            0.15f
    val c = Color(red, green, blue)
    draw(screenOrigin, c.rgb)
  }

  fun draw(screenOrigin: Vector3D, color: Int) {
    GL11.glPushMatrix()

    RenderHelper.disableStandardItemLighting()
    GL11.glEnable(GL11.GL_BLEND)

    val growingFunctionValue = nextValueOfSlowlyGrowingFunction()
    val c = Color(color)
    var red = c.red.toFloat() / 255.0f
    var green = c.green.toFloat() / 255.0f
    var blue = c.blue.toFloat() / 255.0f
    if (Config.colorBlind) {
      red /= 1.8f
      green /= 1.8f
      blue /= 1.8f
    }
    UtilsFX.bindTexture(ParticleEngine.particleTexture)
    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    GL11.glTranslated(screenOrigin.x - 8.0, screenOrigin.y - 8.0, 0.0)
    val part = growingFunctionValue % 8
    val var8 = 0.5f + part.toFloat() / 8.0f
    val var9 = var8 + 0.0624375f
    val var10 = 0.5f
    val var11 = var10 + 0.0624375f
    Tessellator.instance.drawQuads {
      setBrightness(240)
      setColorRGBA_F(red, green, blue, 1.0f)
      addVertexWithUV(0.0, 16.0, screenOrigin.z, var9.toDouble(), var11.toDouble())
      addVertexWithUV(16.0, 16.0, screenOrigin.z, var9.toDouble(), var10.toDouble())
      addVertexWithUV(16.0, 0.0, screenOrigin.z, var8.toDouble(), var10.toDouble())
      addVertexWithUV(0.0, 0.0, screenOrigin.z, var8.toDouble(), var11.toDouble())
    }

    GL11.glPopMatrix()
  }

  /** To simulate particle beating and color changing */
  private fun nextValueOfSlowlyGrowingFunction() =
      FMLClientHandler.instance().client.thePlayer.ticksExisted
}
