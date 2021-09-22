package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.thaumcraft.research.integration.client.container.ResearchTableContainerFactory
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.common.tiles.TileResearchTable

class ResearchTableGui(
    private val player: EntityPlayer,
    private val tileEntity: TileResearchTable,
) : GuiContainer(
    ResearchTableContainerFactory.create(
        player.inventory,
        tileEntity,
        scribeToolsSlotOrigin = ResearchTableInventoryTexture.Slots.scribeToolsOrigin,
        notesSlotOrigin = ResearchTableInventoryTexture.Slots.notesOrigin,
        inventorySlotOrigin = ResearchTableInventoryTexture.inventoryOrigin
    )
) {
    init {
        xSize = ResearchTableInventoryTexture.width
        ySize = ResearchTableInventoryTexture.height

    }
    
    private val origin get() = Vector(x = guiLeft, y = guiTop)

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        ResearchTableInventoryTexture.draw(
            origin = origin,
            zLevel = this.zLevel
        )

        PlayerInventoryTexture.draw(
            origin = origin + ResearchTableInventoryTexture.inventoryOrigin,
            zLevel = this.zLevel
        )
    }

}
