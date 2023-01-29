package elan.tweaks.common.gui.component.texture

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.textures.TextureInstance

class TextureBackgroundUIComponent(
    private val uiOrigin: VectorXY,
    private val texture: TextureInstance
) : BackgroundUIComponent {

  override fun onDrawBackground(
      uiMousePosition: VectorXY,
      partialTicks: Float,
      context: UIContext
  ) = context.drawBlending(texture, uiOrigin)

  companion object {
    fun background(uiOrigin: VectorXY, texture: TextureInstance) =
        TextureBackgroundUIComponent(uiOrigin, texture)
  }
}
