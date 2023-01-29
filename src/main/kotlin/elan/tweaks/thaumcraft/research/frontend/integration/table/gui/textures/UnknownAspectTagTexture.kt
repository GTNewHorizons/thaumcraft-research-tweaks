package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Rgba
import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance

object UnknownAspectTagTexture :
    ThaumcraftTextureInstance(
        "aspects/_unknown.png",
        textureScale = Scale.cube(32),
        scale = Scale.cube(16),
    ) {
  val colorMask = Rgba(0.0f, 0.0f, 0.0f, 0.5f)
}
