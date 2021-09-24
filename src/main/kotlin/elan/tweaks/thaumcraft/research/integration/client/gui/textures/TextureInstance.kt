package elan.tweaks.thaumcraft.research.integration.client.gui.textures

import elan.tweaks.common.gui.Vector

interface TextureInstance {
    val width: Int
    val height: Int
    fun draw(origin: Vector = Vector.ZERO, zLevel: Float)
}
