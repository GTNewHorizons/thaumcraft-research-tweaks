package elan.tweaks.common.gui.textures

import elan.tweaks.common.gui.geometry.Vector3D

interface TextureInstance {
    val width: Int
    val height: Int
    fun draw(origin: Vector3D = Vector3D.ZERO, disableStandardLightning: Boolean = false)
}
