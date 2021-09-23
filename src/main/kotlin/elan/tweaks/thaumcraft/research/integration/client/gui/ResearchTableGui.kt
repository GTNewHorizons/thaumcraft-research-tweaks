package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container

class ResearchTableGui(container: Container) : GuiContainer(container) {
    init {
        xSize = ResearchTableInventoryTexture.width
        ySize = ResearchTableInventoryTexture.inventoryOrigin.y + PlayerInventoryTexture.height
    }

    private val origin get() = Vector(x = guiLeft, y = guiTop)

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        drawInventory()

        drawTable()
    }

    private fun drawTable() {
        PlayerInventoryTexture.draw(
            origin = origin + ResearchTableInventoryTexture.inventoryOrigin,
            zLevel = this.zLevel
        )
        
        
    }

    private fun drawInventory() {
        ResearchTableInventoryTexture.draw(
            origin = origin,
            zLevel = this.zLevel
        )
    }

}
