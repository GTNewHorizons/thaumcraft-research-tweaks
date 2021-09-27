package elan.tweaks.common.gui.component.texture

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.TextureInstance

class TextureBackgroundUIComponent(
    private val uiOrigin: VectorXY,
    private val texture: TextureInstance
) : BackgroundUIComponent {

    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) =
        texture.draw(
            origin = context.toScreenOrigin(uiOrigin)
        )

    companion object {
        fun background(uiOrigin: VectorXY, texture: TextureInstance) = TextureBackgroundUIComponent(uiOrigin, texture)
    }

}
