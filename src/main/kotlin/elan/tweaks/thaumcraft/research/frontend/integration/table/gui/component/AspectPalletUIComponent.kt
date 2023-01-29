package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.ClickableUIComponent
import elan.tweaks.common.gui.component.MouseOverUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.component.dragndrop.DragClickableDestinationUIComponent
import elan.tweaks.common.gui.component.dragndrop.DraggableSourceUIComponent
import elan.tweaks.common.gui.component.dragndrop.DropDestinationUIComponent
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.layout.grid.GridLayout
import elan.tweaks.common.gui.peripheral.MouseButton
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectPalletPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort.Knowledge
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout.AspectTooltipLayout
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout.AspectTooltipLayout.firstTagOffset
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout.AspectTooltipLayout.secondTagOffset
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.AspectBackgroundTexture
import net.minecraft.client.gui.inventory.GuiContainer.isCtrlKeyDown
import net.minecraft.client.gui.inventory.GuiContainer.isShiftKeyDown
import thaumcraft.api.aspects.Aspect

class AspectPalletUIComponent(
    private val aspectGrid: GridLayout<Aspect>,
    private val pallet: AspectPalletPort,
    private val researcher: ResearcherKnowledgePort
) :
    BackgroundUIComponent,
    MouseOverUIComponent,
    ClickableUIComponent,
    DraggableSourceUIComponent,
    DropDestinationUIComponent,
    DragClickableDestinationUIComponent {

  override fun onDrawBackground(
      uiMousePosition: VectorXY,
      partialTicks: Float,
      context: UIContext
  ) =
      aspectGrid.asOriginList().forEach { (uiOrigin, aspect) ->
        val (amount, bonusAmount) = pallet.amountAndBonusOf(aspect)

        context.drawTag(
            aspect,
            amount,
            bonusAmount,
            alpha = if (pallet.isDrainedOf(aspect)) 0.33f else 1.0f,
            uiPosition = uiOrigin)
      }

  override fun onMouseOver(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) =
      whenAspectAt(uiMousePosition) { aspect ->
        drawTooltip(aspect, uiOrigin = uiMousePosition, context)
      }

  private fun drawTooltip(aspect: Aspect, uiOrigin: VectorXY, context: UIContext) {
    val tooltipOrigin = uiOrigin + AspectTooltipLayout.textBoxOffset
    context.drawTooltip(tooltipOrigin, aspect.name, aspect.localizedDescription)

    if (aspect.hasComponents() && researcher.hasDiscovered(Knowledge.ResearchExpertise)) {
      val firstBackgroundOrigin = uiOrigin + AspectTooltipLayout.firstTagBackgroundOffset
      context.drawBlending(AspectBackgroundTexture, firstBackgroundOrigin)
      context.drawTag(aspect.components[0], uiPosition = uiOrigin + firstTagOffset)

      val secondBackgroundOrigin = uiOrigin + AspectTooltipLayout.secondTagBackgroundOffset
      context.drawBlending(AspectBackgroundTexture, secondBackgroundOrigin)
      context.drawTag(aspect.components[1], uiPosition = uiOrigin + secondTagOffset)
    }
  }

  private fun Aspect.hasComponents() = !isPrimal && components != null && components.isNotEmpty()

  override fun onMouseClicked(uiMousePosition: VectorXY, button: MouseButton, context: UIContext) {
    whenAspectAt(uiMousePosition) { aspect ->
      if (button is MouseButton.Left && isIntendingToDeriveAspect()) {
        derive(aspect).onSuccess { context.playCombine() }
      }
    }
  }

  private fun derive(aspect: Aspect) =
      if (isIntendingToBatch()) pallet.deriveBatch(aspect) else pallet.derive(aspect)

  override fun onDrag(uiMousePosition: VectorXY, context: UIContext): Any? {
    val aspect = aspectGrid[uiMousePosition] ?: return null
    if (pallet.isDrainedOf(aspect) || isIntendingToDeriveAspect()) return null

    context.playButtonAspect()
    return aspect
  }

  private fun isIntendingToDeriveAspect() = isShiftKeyDown()

  private fun UIContext.playButtonAspect() {
    playSound("thaumcraft:hhoff", 0.2f, 1.0f + random.nextFloat() * 0.1f, false)
  }

  override fun onDropped(
      draggable: Any,
      uiMousePosition: VectorXY,
      partialTicks: Float,
      context: UIContext
  ) {
    if (draggable !is Aspect) return

    whenAspectAt(uiMousePosition) { targetAspect ->
      combine(draggable, targetAspect).onSuccess { context.playCombine() }
    }
  }

  override fun onDragClick(
      draggable: Any,
      uiMousePosition: VectorXY,
      button: MouseButton,
      context: UIContext
  ) {
    if (draggable !is Aspect || button !is MouseButton.Right) return

    whenAspectAt(uiMousePosition) { targetAspect ->
      combine(draggable, targetAspect).onSuccess { context.playCombine() }
    }
  }

  private fun combine(draggable: Aspect, targetAspect: Aspect) =
      if (isIntendingToBatch()) pallet.combineBatch(draggable, targetAspect)
      else pallet.combine(draggable, targetAspect)

  private fun isIntendingToBatch() = isCtrlKeyDown()

  private inline fun whenAspectAt(uiMousePosition: VectorXY, action: (Aspect) -> Unit) {
    aspectGrid[uiMousePosition]?.run(action)
  }

  private fun UIContext.playCombine() = apply {
    playSound(soundName = "thaumcraft:hhon", volume = 0.3f, pitch = 1.0f, distanceDelay = false)
  }
}
