package elan.tweaks.thaumcraft.research.integration.table.gui.textures

import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance

object ParchmentTexture : ThaumcraftTextureInstance(
    "misc/parchment3.png",
    textureWidth = 256, textureHeight = 256,
    u = 0, v = 0,
    width = 150, height = 150
) {
    val centerOrigin = Vector2D(width / 2, height / 2)
}
