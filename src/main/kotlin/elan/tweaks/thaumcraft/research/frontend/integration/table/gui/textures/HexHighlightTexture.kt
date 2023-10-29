package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Rgba
import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11

object HexHighlightTexture :
    ThaumcraftTextureInstance(
        "gui/hex2.png",
        textureScale = Scale.cube(32),
        scale = Scale.cube(16),
    ) {
  val colorMask = Rgba(1f, 1f, 1f, 1f)

  override fun beforeGL() {
    GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, 1)
  }

  override fun before(tessellator: Tessellator) {
    tessellator.setBrightness(240)
  }

  override fun afterGL() {
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, 771)
    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f)
  }
}
