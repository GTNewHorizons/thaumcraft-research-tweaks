package elan.tweaks.thaumcraft.research.integration.client.gui.textures

import elan.tweaks.thaumcraft.research.integration.client.gui.Vector

interface TextureInstance {
    val width: Int
    val height: Int
    fun draw(origin: Vector, zLevel: Float)
}
