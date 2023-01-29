package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.UV
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance

class RuneTexture private constructor(index: Int) :
    ThaumcraftTextureInstance(
        "misc/script.png",
        textureScale = Scale(width = 256, height = 16),
        uv = UV(u = 16 * index, v = 0),
        uvScale = Scale.cube(16),
        scale = Scale.cube(10)) {

  companion object {
    val offsetToFitInHexCenter = Vector2D(3, 3)

    private val allRunes = (0..15).map(::RuneTexture)

    fun random() = allRunes.random()
  }
}
