package elan.tweaks.common.gui.component

import elan.tweaks.common.gui.dto.Rgba
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.textures.TextureInstance
import java.util.Random
import thaumcraft.api.aspects.Aspect

interface UIContext {
  val random: Random

  fun drawBlending(
      texture: TextureInstance,
      uiPosition: VectorXY,
      colorMask: Rgba = Rgba(1f, 1f, 1f, 1f)
  )

  fun drawWithShadow(text: String, uiPosition: VectorXY = Vector2D.ZERO)

  fun drawTag(aspect: Aspect, amount: Int, uiPosition: VectorXY = Vector2D.ZERO)
  fun drawTag(
      aspect: Aspect,
      amount: Int = 0,
      bonus: Int = 0,
      blend: Int = 771,
      alpha: Float = 1f,
      uiPosition: VectorXY = Vector2D.ZERO
  )

  fun drawTagMonochrome(aspect: Aspect, alpha: Float = 0.66f, uiPosition: VectorXY = Vector2D.ZERO)

  fun drawTooltip(uiPosition: VectorXY = Vector2D.ZERO, vararg text: String)
  fun drawTooltipCentered(uiCenterPosition: VectorXY = Vector2D.ZERO, vararg text: String)
  fun drawTooltipVerticallyCentered(uiCenterPosition: VectorXY = Vector2D.ZERO, vararg text: String)

  fun drawOrb(uiPosition: VectorXY, color: Int)
  fun drawOrb(uiPosition: VectorXY)

  fun drawLine(from: VectorXY, to: VectorXY)

  fun playSound(soundName: String, volume: Float, pitch: Float, distanceDelay: Boolean)
}
