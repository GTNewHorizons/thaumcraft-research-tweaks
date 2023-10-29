package elan.tweaks.thaumcraft.research.frontend.integration.table

import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.Rgba
import elan.tweaks.common.gui.dto.Vector3D
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.fx.LineParticle
import elan.tweaks.common.gui.fx.OrbParticle
import elan.tweaks.common.gui.rendering.AspectRenderer
import elan.tweaks.common.gui.rendering.TextureRenderer
import elan.tweaks.common.gui.rendering.TooltipRenderer
import elan.tweaks.common.gui.textures.TextureInstance
import java.util.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.world.World
import thaumcraft.api.aspects.Aspect

class TableUIContext(
    private val screenOrigin: Vector3D,
    private val fontRenderer: FontRenderer,
) : UIContext {

  private val renderViewEntity: EntityLivingBase by lazy {
    Minecraft.getMinecraft().renderViewEntity
  }
  private val world: World by lazy { renderViewEntity.worldObj }
  override val random: Random by lazy { world.rand }

  override fun drawBlending(texture: TextureInstance, uiPosition: VectorXY, colorMask: Rgba) {
    TextureRenderer.drawBlending(texture, screenOrigin + uiPosition, colorMask)
  }

  override fun drawWithShadow(text: String, uiPosition: VectorXY) {
    fontRenderer.drawStringWithShadow(
        text, screenOrigin.x + uiPosition.x, screenOrigin.y + uiPosition.y, -1)
  }

  override fun drawTag(aspect: Aspect, amount: Int, uiPosition: VectorXY) {
    AspectRenderer.drawTag(screenOrigin + uiPosition, aspect, amount)
  }

  override fun drawTag(
      aspect: Aspect,
      amount: Int,
      bonus: Int,
      blend: Int,
      alpha: Float,
      uiPosition: VectorXY
  ) {
    AspectRenderer.drawTag(
        screenOrigin + uiPosition,
        aspect,
        amount = amount,
        bonus = bonus,
        blend = blend,
        alpha = alpha)
  }

  override fun drawTagMonochrome(aspect: Aspect, alpha: Float, uiPosition: VectorXY) {
    AspectRenderer.drawTagMonochrome(screenOrigin + uiPosition, aspect, alpha)
  }

  override fun drawTooltip(uiPosition: VectorXY, vararg text: String) {
    TooltipRenderer.draw(
        text,
        origin = screenOrigin + uiPosition,
        TooltipRenderer.TextColors.LIGHT_BLUE,
        fontRenderer)
  }

  override fun drawTooltipCentered(uiCenterPosition: VectorXY, vararg text: String) {
    TooltipRenderer.drawCentered(
        text,
        center = screenOrigin + uiCenterPosition,
        TooltipRenderer.TextColors.LIGHT_BLUE,
        fontRenderer)
  }

  override fun drawTooltipVerticallyCentered(uiCenterPosition: VectorXY, vararg text: String) {
    TooltipRenderer.drawVerticallyCentered(
        text,
        center = screenOrigin + uiCenterPosition,
        TooltipRenderer.TextColors.LIGHT_BLUE,
        fontRenderer)
  }

  override fun drawOrb(uiPosition: VectorXY, color: Int) {
    OrbParticle.draw(screenOrigin + uiPosition, color)
  }

  override fun drawOrb(uiPosition: VectorXY) {
    OrbParticle.draw(screenOrigin + uiPosition)
  }

  override fun drawLine(from: VectorXY, to: VectorXY) {
    LineParticle.draw(screenOrigin + from, screenOrigin + to)
  }

  override fun playSound(soundName: String, volume: Float, pitch: Float, distanceDelay: Boolean) {
    val entity = renderViewEntity
    world.playSound(entity.posX, entity.posY, entity.posZ, soundName, volume, pitch, distanceDelay)
  }
}
