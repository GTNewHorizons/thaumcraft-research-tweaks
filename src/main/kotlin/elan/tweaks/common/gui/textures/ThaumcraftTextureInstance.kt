package elan.tweaks.common.gui.textures

import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.UV
import net.minecraft.util.ResourceLocation

abstract class ThaumcraftTextureInstance(
    path: String,
    override val textureScale: Scale,
    override val uv: UV = UV(u = 0, v = 0),
    override val uvScale: Scale = textureScale,
    override val scale: Scale = uvScale
) : TextureInstance {
  companion object {
    private const val DOMAIN = "thaumcraft"
  }

  override val resourceLocation = ResourceLocation(DOMAIN, "textures/$path")
}
