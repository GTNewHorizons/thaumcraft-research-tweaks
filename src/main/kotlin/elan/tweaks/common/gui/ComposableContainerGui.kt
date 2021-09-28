package elan.tweaks.common.gui

import elan.tweaks.common.gui.component.*
import elan.tweaks.common.gui.component.dragndrop.DragAndDropUIComponent
import elan.tweaks.common.gui.component.dragndrop.DragClickableDestinationUIComponent
import elan.tweaks.common.gui.component.dragndrop.DraggableSourceUIComponent
import elan.tweaks.common.gui.component.dragndrop.DropDestinationUIComponent
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.Vector3D
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.peripheral.MouseButton
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.inventory.Container

class ComposableContainerGui(
    container: Container,
    components: List<UIComponent>,
    xSize: Int,
    ySize: Int
) : GuiContainer(container), UIContext {

    init {
        super.xSize = xSize
        super.ySize = ySize
    }

    override val fontRenderer: FontRenderer get() = fontRendererObj

    private val backgrounds = components.filterIsInstance<BackgroundUIComponent>()
    private val screens = components.filterIsInstance<ScreenUIComponent>()
    private val clickables = components.filterIsInstance<ClickableUIComponent>()
    private val draggableSources = components.filterIsInstance<DraggableSourceUIComponent>()
    private val dropDestinations = components.filterIsInstance<DropDestinationUIComponent>()
    private val dragClickables = components.filterIsInstance<DragClickableDestinationUIComponent>()
    private val dragAndDrops = components.filterIsInstance<DragAndDropUIComponent>()

    private val uiScreenOrigin get() = Vector3D(x = guiLeft, y = guiTop, z = zLevel.toDouble())

    override fun toScreenOrigin(vectorXY: VectorXY): Vector3D =
        uiScreenOrigin + vectorXY

    override fun setItemRenderZLevel(z: Float) {
        itemRender.zLevel = z
    }

    override fun playSoundOnEntity(soundName: String, volume: Float, pitch: Float, distanceDelay: Boolean) {
        val entity = mc.renderViewEntity
        entity.worldObj.playSound(
            entity.posX, entity.posY, entity.posZ,
            soundName,
            volume, pitch, distanceDelay
        )
    }

    override fun drawQuads(configureTesselation: Tessellator.() -> Unit) {
        Tessellator
            .instance
            .also { it.startDrawingQuads() }
            .apply(configureTesselation)
            .draw()
    }

    override fun nextRandomFloat(): Float =
        mc.renderViewEntity.worldObj.rand.nextFloat()

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        val uiMousePosition = uiMousePosition(mouseX, mouseY)
        drawScreens(uiMousePosition, partialTicks)
        handleDragAndDrops(uiMousePosition, partialTicks)

    }

    private fun handleDragAndDrops(uiMousePosition: Vector3D, partialTicks: Float) =
        if (MouseButton.Left.isDown()) handleDragging(uiMousePosition, partialTicks)
        else handleDropping(uiMousePosition, partialTicks)

    private fun handleDragging(uiMousePosition: Vector3D, partialTicks: Float) {
        val draggables = draggableSources.mapNotNull { it.onDrag(uiMousePosition, partialTicks, context = this) }
        dragAndDrops.forEach { dragAndDrop ->
            dragAndDrop.onAttemptDrag(draggables, uiMousePosition, context = this)

            dragAndDrop.onDragging(uiMousePosition, context = this)
        }
    }

    private fun handleDropping(uiMousePosition: Vector3D, partialTicks: Float) {
        val draggables = dragAndDrops.mapNotNull { it.onDropping(context = this) }
        dropDestinations.forEach { destination ->
            draggables.forEach { draggable ->
                destination.onDropped(draggable, uiMousePosition, partialTicks, context = this)
            }
        }
    }

    private fun drawScreens(uiMousePosition: Vector3D, partialTicks: Float) {
        screens.forEach { it.onDrawScreen(uiMousePosition, partialTicks, context = this) }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        val mousePosition = uiMousePosition(mouseX, mouseY)
        backgrounds.forEach { it.onDrawBackground(mousePosition, partialTicks, context = this) }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, buttonIndex: Int) {
        super.mouseClicked(mouseX, mouseY, buttonIndex)

        val uiMousePosition = uiMousePosition(mouseX, mouseY)
        val button = MouseButton.of(buttonIndex)
        handleClickables(uiMousePosition, button)
        handleDragClickables(uiMousePosition, button)
    }

    private fun handleDragClickables(uiMousePosition: Vector3D, button: MouseButton) {
        val draggables = dragAndDrops.mapNotNull { it.onDragClick(context = this) }
        dragClickables.forEach { destination ->
            draggables.forEach { draggable ->
                destination.onDragClick(draggable, uiMousePosition, button, context = this)
            }
        }
    }

    private fun handleClickables(uiMousePosition: Vector3D, button: MouseButton) {
        clickables.forEach { it.onMouseClicked(uiMousePosition, button, context = this) }
    }


    private fun uiMousePosition(mouseX: Int, mouseY: Int) = 
        Vector2D(mouseX, mouseY) - uiScreenOrigin
}
