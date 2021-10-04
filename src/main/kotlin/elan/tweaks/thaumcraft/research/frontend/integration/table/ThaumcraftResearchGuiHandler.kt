package elan.tweaks.thaumcraft.research.frontend.integration.table

import cpw.mods.fml.common.network.IGuiHandler
import elan.tweaks.thaumcraft.research.frontend.integration.table.ThaumcraftResearchGuiHandler.IDs.RESEARCH_TABLE
import elan.tweaks.thaumcraft.research.frontend.integration.table.container.ResearchTableContainerFactory
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.ResearchTableGuiFactory
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import thaumcraft.client.gui.GuiResearchTable
import thaumcraft.common.container.ContainerResearchTable
import thaumcraft.common.tiles.TileResearchTable

// TODO make this a research table gui specific handler which always provides table components and reference it instead of mod when creating gui
class ThaumcraftResearchGuiHandler : IGuiHandler {

    object IDs {
        const val RESEARCH_TABLE = 0
    }
    
    override fun getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? =
        if (id == RESEARCH_TABLE && x == -142 && y == 68 && z == 239)
            ContainerResearchTable(player.inventory, world.getTileEntity(x, y, z) as TileResearchTable) // TODO: remove old GUI debug and coords in if
        else if(id == RESEARCH_TABLE)
            ResearchTableContainerFactory.create(player.inventory, world.getTableTileEntity(x, y, z))
        else null
    
    override fun getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? =
        if (world is WorldClient && id == RESEARCH_TABLE && x == -142 && y == 68 && z == 239)
            GuiResearchTable(player, world.getTileEntity(x, y, z) as TileResearchTable) // TODO: remove old GUI debug and coords in if
        else if(id == RESEARCH_TABLE)
            ResearchTableGuiFactory.create(player, world.getTableTileEntity(x, y, z))
        else
            null
    
    private fun World.getTableTileEntity(x: Int, y: Int, z: Int) =
        getTileEntity(x, y, z) as TileResearchTable

}

