package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.Vector3D
import elan.tweaks.common.gui.geometry.VectorXY
import net.minecraft.client.gui.FontRenderer

interface UIContext {

    val fontRenderer: FontRenderer

    fun setItemRenderZLevel(z: Float)

    fun playSoundOnEntity(
        soundName: String,
        volume: Float,
        pitch: Float,
        distanceDelay: Boolean
    )
    
    fun toScreenOrigin(vectorXY: VectorXY): Vector3D

    fun nextRandomFloat(): Float
}
