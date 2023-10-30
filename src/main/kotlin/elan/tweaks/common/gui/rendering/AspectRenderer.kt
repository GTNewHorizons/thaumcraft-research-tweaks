package elan.tweaks.common.gui.rendering

import elan.tweaks.common.gui.dto.Vector3D
import net.minecraft.client.renderer.RenderHelper
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.lib.UtilsFX

object AspectRenderer {
  fun drawTag(
      screenOrigin: Vector3D,
      aspect: Aspect?,
      amount: Int = 0,
      bonus: Int = 0,
      blend: Int = 771,
      alpha: Float = 1f
  ) {
    UtilsFX.drawTag(
        screenOrigin.x,
        screenOrigin.y,
        aspect,
        amount.toFloat(),
        bonus,
        screenOrigin.z,
        blend,
        alpha,
        false)
    RenderHelper.disableStandardItemLighting()
  }

  fun drawTagMonochrome(screenOrigin: Vector3D, aspect: Aspect?, alpha: Float) {
    UtilsFX.drawTag(
        screenOrigin.x.toDouble(),
        screenOrigin.y.toDouble(),
        aspect,
        ZERO_AMOUNT,
        ZERO_BONUS,
        screenOrigin.z,
        BLEND,
        alpha,
        true)
    RenderHelper.disableStandardItemLighting()
  }

  private const val BLEND = 771
  private const val ZERO_BONUS = 0
  private const val ZERO_AMOUNT = 0f
}
