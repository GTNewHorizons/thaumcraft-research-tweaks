package elan.tweaks.common.gui

import elan.tweaks.common.gui.component.*
import elan.tweaks.common.gui.component.dragndrop.DragAndDropUIComponent
import elan.tweaks.common.gui.component.dragndrop.DragClickableDestinationUIComponent
import elan.tweaks.common.gui.component.dragndrop.DraggableSourceUIComponent
import elan.tweaks.common.gui.component.dragndrop.DropDestinationUIComponent
import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.dto.Vector3D
import elan.tweaks.common.gui.peripheral.MouseButton
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container

class ComposableContainerGui(
    scale: Scale,
    container: Container,
    components: List<UIComponent>,
    private val contextProvider: (Vector3D, FontRenderer) -> UIContext
) : GuiContainer(container) {

  init {
    super.xSize = scale.width
    super.ySize = scale.height
  }

  private val backgrounds = components.filterIsInstance<BackgroundUIComponent>()
  private val foregrounds = components.filterIsInstance<ForegroundUIComponent>()
  private val tickables = components.filterIsInstance<TickingUIComponent>()
  private val mouseOverables = components.filterIsInstance<MouseOverUIComponent>()
  private val clickables = components.filterIsInstance<ClickableUIComponent>()
  private val draggableSources = components.filterIsInstance<DraggableSourceUIComponent>()
  private val dropDestinations = components.filterIsInstance<DropDestinationUIComponent>()
  private val dragClickables = components.filterIsInstance<DragClickableDestinationUIComponent>()
  private val dragAndDrops = components.filterIsInstance<DragAndDropUIComponent>()

  private val uiScreenOrigin
    get() = Vector3D(x = guiLeft, y = guiTop, z = zLevel.toDouble())
  private val context
    get() = contextProvider(uiScreenOrigin, fontRendererObj)

  override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
    super.drawScreen(mouseX, mouseY, partialTicks)

    val uiMousePosition = uiMousePosition(mouseX, mouseY)
    handleMouseOver(uiMousePosition, partialTicks)
    handleDragAndDrops(uiMousePosition, partialTicks)
  }

  private fun handleDragAndDrops(uiMousePosition: Vector3D, partialTicks: Float) =
      if (MouseButton.Left.isDown()) handleDragging(uiMousePosition)
      else handleDropping(uiMousePosition, partialTicks)

  private fun handleDragging(uiMousePosition: Vector3D) {
    dragAndDrops.forEach { dragAndDrop -> dragAndDrop.onDragging(uiMousePosition, context) }
  }

  private fun handleDropping(uiMousePosition: Vector3D, partialTicks: Float) {
    val draggables = dragAndDrops.mapNotNull { it.onDropping(context) }
    dropDestinations.forEach { destination ->
      draggables.forEach { draggable ->
        destination.onDropped(draggable, uiMousePosition, partialTicks, context)
      }
    }
  }

  private fun handleMouseOver(uiMousePosition: Vector3D, partialTicks: Float) {
    mouseOverables.forEach { it.onMouseOver(uiMousePosition, partialTicks, context) }
  }

  override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    val mousePosition = uiMousePosition(mouseX, mouseY)
    drawBackgrounds(mousePosition, partialTicks)
    callTickables(partialTicks)
  }

  private fun drawBackgrounds(mousePosition: Vector3D, partialTicks: Float) {
    backgrounds.forEach { it.onDrawBackground(mousePosition, partialTicks, context) }
  }

  override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
    val mousePosition = uiMousePosition(mouseX, mouseY)
    val scale = Scale(width = width, height = height)
    foregrounds.forEach { it.onDrawForeground(mousePosition, scale, context) }
  }

  private fun callTickables(partialTicks: Float) {
    tickables.forEach { it.onTick(partialTicks, context) }
  }

  override fun mouseClicked(mouseX: Int, mouseY: Int, buttonIndex: Int) {
    super.mouseClicked(mouseX, mouseY, buttonIndex)

    val uiMousePosition = uiMousePosition(mouseX, mouseY)
    val button = MouseButton.of(buttonIndex)
    handleClickables(uiMousePosition, button)
    handleDraggables(button, uiMousePosition)
  }

  private fun handleDraggables(button: MouseButton, uiMousePosition: Vector3D) {
    if (button is MouseButton.Left) handleDragAttempt(uiMousePosition)
    else handleDragClickables(uiMousePosition, button)
  }

  private fun handleDragClickables(uiMousePosition: Vector3D, button: MouseButton) {
    val draggables = dragAndDrops.mapNotNull { it.onDragClick(context) }
    dragClickables.forEach { destination ->
      draggables.forEach { draggable ->
        destination.onDragClick(draggable, uiMousePosition, button, context)
      }
    }
  }

  private fun handleDragAttempt(uiMousePosition: Vector3D) {
    val draggables = draggableSources.mapNotNull { it.onDrag(uiMousePosition, context) }
    dragAndDrops.forEach { dragAndDrop ->
      dragAndDrop.onAttemptDrag(draggables, uiMousePosition, context)
    }
  }

  private fun handleClickables(uiMousePosition: Vector3D, button: MouseButton) {
    clickables.forEach { it.onMouseClicked(uiMousePosition, button, context) }
  }

  private fun uiMousePosition(mouseX: Int, mouseY: Int) = Vector2D(mouseX, mouseY) - uiScreenOrigin

  companion object {
    fun gui(
        scale: Scale,
        container: Container,
        components: List<UIComponent>,
        contextProvider: (Vector3D, FontRenderer) -> UIContext
    ) =
        ComposableContainerGui(
            scale, container = container, components = components, contextProvider)
  }
}
