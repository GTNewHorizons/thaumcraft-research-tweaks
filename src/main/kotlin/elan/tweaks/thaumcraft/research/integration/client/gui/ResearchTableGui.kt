package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.common.tiles.TileResearchTable

class ResearchTableGui(
    private val player: EntityPlayer,
    private val tileEntity: TileResearchTable,
) : GuiContainer(
    ResearchTableContainerFactory.create(player.inventory, tileEntity)
) {
    init {
        xSize = 255
        ySize = 255
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        val inventoryXOffset = (xSize - PlayerInventoryTexture.width) / 2
        val inventoryYOffset = ySize - PlayerInventoryTexture.height

        PlayerInventoryTexture.draw(
            origin = originMatchingSlots(),
            zLevel = this.zLevel
        )
    }

    private fun originMatchingSlots() =
        Vector(guiLeft + 40, guiTop + 168)
}
