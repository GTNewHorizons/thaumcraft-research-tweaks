package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.geometry.Vector3D
import elan.tweaks.common.gui.geometry.VectorXY
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.Tessellator

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
    
    // Probably should move this to utility object
    fun drawQuads(configureTesselation: Tessellator.() -> Unit)
}
