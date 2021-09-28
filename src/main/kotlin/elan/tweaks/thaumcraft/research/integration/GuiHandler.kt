package elan.tweaks.thaumcraft.research.integration

import cpw.mods.fml.common.network.IGuiHandler
import elan.tweaks.thaumcraft.research.integration.table.gui.ResearchTableGuiFactory
import elan.tweaks.thaumcraft.research.integration.aspect.pool.AspectPalletFactory
import elan.tweaks.thaumcraft.research.integration.table.container.ResearchTableContainerFactory
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import thaumcraft.client.gui.GuiResearchTable
import thaumcraft.common.container.ContainerResearchTable
import thaumcraft.common.tiles.TileResearchTable

class GuiHandler : IGuiHandler {

    object IDs {
        const val RESEARCH_TABLE = 0
    }
    
    override fun getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? =
        if (id == IDs.RESEARCH_TABLE && x == -142 && y == 68 && z == 239)
            ResearchTableContainerFactory.create(player.inventory, world.getTableTileEntity(x, y, z))
        else
            ContainerResearchTable(player.inventory, world.getTileEntity(x, y, z) as TileResearchTable) // TODO: remove old GUI debug and coords in if

    override fun getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? =
        if (world is WorldClient && id == IDs.RESEARCH_TABLE && x == -142 && y == 68 && z == 239) 
            createResearchTableGui(player, world.getTableTileEntity(x, y, z)) 
        else 
            GuiResearchTable(player, world.getTileEntity(x, y, z) as TileResearchTable) // TODO: remove old GUI debug and coords in if

    private fun createResearchTableGui(
        player: EntityPlayer,
        tableEntity: TileResearchTable
    ) = ResearchTableGuiFactory.create(
        player,
        AspectPalletFactory.create(player, tableEntity),
        tableEntity
    )

    private fun World.getTableTileEntity(x: Int, y: Int, z: Int) =
        getTileEntity(x, y, z) as TileResearchTable

}

