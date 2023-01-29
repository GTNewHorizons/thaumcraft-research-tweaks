package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Rectangle
import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance

object ParchmentTexture :
    ThaumcraftTextureInstance(
        "misc/parchment3.png", textureScale = Scale.cube(256), uvScale = Scale.cube(150)) {
  val centerOrigin = Vector2D(scale.width / 2, scale.height / 2)

  val drawableBounds =
      Rectangle(origin = Vector2D(x = 15, y = 10), scale = Scale(width = 125, height = 130))
}
