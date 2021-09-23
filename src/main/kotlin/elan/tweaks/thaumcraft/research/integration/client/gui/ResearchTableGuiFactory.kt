package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.thaumcraft.research.integration.client.container.ResearchTableContainerFactory
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.world.World
import thaumcraft.common.tiles.TileResearchTable

object ResearchTableGuiFactory {

    fun createFor(
        player: EntityPlayer,
        world: World, x: Int, y: Int, z: Int
    ) =
        ResearchTableGui(
            createContainer(player, world, x, y, z),
            player,
            world.getTableTileEntity(x, y, z)
        )

    fun createContainer(
        player: EntityPlayer,
        world: World, x: Int, y: Int, z: Int
    ) =
        createContainer(
            player.inventory,
            world.getTableTileEntity(x, y, z),
        )

    private fun World.getTableTileEntity(x: Int, y: Int, z: Int) =
        getTileEntity(x, y, z) as TileResearchTable


    private fun createContainer(
        playerInventory: InventoryPlayer,
        researchTableTileEntity: TileResearchTable
    ) =
        ResearchTableContainerFactory.create(
            playerInventory,
            researchTableTileEntity,
            scribeToolsSlotOrigin = ResearchTableInventoryTexture.Slots.scribeToolsOrigin,
            notesSlotOrigin = ResearchTableInventoryTexture.Slots.notesOrigin,
            inventorySlotOrigin = ResearchTableInventoryTexture.inventoryOrigin,
        )

}
