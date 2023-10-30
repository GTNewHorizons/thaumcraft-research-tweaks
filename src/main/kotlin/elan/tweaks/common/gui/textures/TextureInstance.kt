package elan.tweaks.common.gui.textures

import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.UV
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.ResourceLocation

interface TextureInstance {
  val scale: Scale
  val uv: UV
  val uvScale: Scale
  val textureScale: Scale

  val resourceLocation: ResourceLocation

  fun beforeGL() {}
  fun before(tessellator: Tessellator) {}
  fun afterGL() {}
}
