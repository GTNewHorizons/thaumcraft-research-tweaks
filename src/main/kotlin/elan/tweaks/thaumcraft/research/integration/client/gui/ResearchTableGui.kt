package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.common.gui.component.*
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.Vector3D
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container

class ResearchTableGui(
    container: Container,
    private val components: List<UIComponent>
) : GuiContainer(container), UIContext {

    init {
        xSize = ResearchTableInventoryTexture.width
        ySize = ResearchTableInventoryTexture.inventoryOrigin.y + PlayerInventoryTexture.height
    }

    override val fontRenderer: FontRenderer
        get() = fontRendererObj

    private val backgroundComponents by lazy { components.filterIsInstance<BackgroundUIComponent>() }
    private val screenComponents by lazy { components.filterIsInstance<ScreenUIComponent>() }
    private val clickableComponents by lazy { components.filterIsInstance<ClickableUIComponent>() }

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

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        drawInventory()
        drawTable()

        val mousePosition = uiMousePosition(mouseX, mouseY)
        backgroundComponents.forEach { it.onDrawBackground(mousePosition, partialTicks, this) }
    }

    private fun drawInventory() {
        PlayerInventoryTexture.draw(
            origin = toScreenOrigin(ResearchTableInventoryTexture.inventoryOrigin)
        )
    }

    private fun drawTable() {
        ResearchTableInventoryTexture.draw(
            origin = uiScreenOrigin
        )
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        val mousePosition = uiMousePosition(mouseX, mouseY)
        screenComponents.forEach { it.onDrawScreen(mousePosition, partialTicks, this) }
    }


    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        val mousePosition = uiMousePosition(mouseX, mouseY)
        clickableComponents.forEach { it.onMouseClicked(mousePosition, mouseButton, this) }
    }
    
    private fun uiMousePosition(mouseX: Int, mouseY: Int) = 
        Vector2D(mouseX, mouseY) - uiScreenOrigin
}
