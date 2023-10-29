package elan.tweaks.common.gui.rendering

import elan.tweaks.common.ext.drawQuads
import elan.tweaks.common.gui.dto.Rgba
import elan.tweaks.common.gui.dto.Vector3D
import elan.tweaks.common.gui.textures.TextureInstance
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11

/**
 * Based on
 * https://github.com/TimeConqueror/LootGames/blob/64c1bfbae24a0c6c14ef883c4c8d558d3778ee22/src/main/java/ru/timeconqueror/timecore/api/util/client/DrawHelper.java#L6
 */
object TextureRenderer {
  private val textureManager by lazy { Minecraft.getMinecraft().textureManager }

  fun drawBlending(
      texture: TextureInstance,
      origin: Vector3D,
      colorMask: Rgba = Rgba(1f, 1f, 1f, 1f)
  ) {
    GL11.glPushMatrix()
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glColor4f(colorMask.r, colorMask.g, colorMask.b, colorMask.a)

    texture.bindAndDraw(origin, colorMask)

    GL11.glPopMatrix()
  }

  private fun TextureInstance.bindAndDraw(origin: Vector3D, colorMask: Rgba) {
    textureManager.bindTexture(resourceLocation)
    beforeGL()
    tessellate(origin, colorMask)
    afterGL()
  }

  private fun TextureInstance.tessellate(position: Vector3D, colorMask: Rgba) {
    val portionXFactor = 1.0 / textureScale.width
    val portionYFactor = 1.0 / textureScale.height
    Tessellator.instance.drawQuads {
      before(this)
      setColorRGBA_F(colorMask.r, colorMask.g, colorMask.b, colorMask.a)
      addVertexWithUV(
          position.x.toDouble(),
          position.y.toDouble(),
          position.z,
          uv.u * portionXFactor,
          uv.v * portionYFactor)
      addVertexWithUV(
          position.x.toDouble(),
          (position.y + scale.height).toDouble(),
          position.z,
          uv.u * portionXFactor,
          (uv.v + uvScale.height) * portionYFactor)
      addVertexWithUV(
          (position.x + scale.width).toDouble(),
          (position.y + scale.height).toDouble(),
          position.z,
          (uv.u + uvScale.width) * portionXFactor,
          (uv.v + uvScale.height) * portionYFactor)
      addVertexWithUV(
          (position.x + scale.width).toDouble(),
          position.y.toDouble(),
          position.z,
          (uv.u + uvScale.width) * portionXFactor,
          uv.v * portionYFactor)
    }
  }
}
