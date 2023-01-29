package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Rgba
import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11

object HexTexture :
    ThaumcraftTextureInstance(
        "gui/hex1.png",
        textureScale = Scale.cube(32),
        scale = Scale.cube(16),
    ) {
  const val SIZE_PIXELS = 9
  val colorMask = Rgba(1f, 1f, 1f, 0.25f)

  override fun beforeGL() {
    GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f)
  }

  override fun before(tessellator: Tessellator) {
    tessellator.setBrightness(240)
  }

  override fun afterGL() {
    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f)
  }
}
