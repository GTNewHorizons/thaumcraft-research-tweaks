package elan.tweaks.thaumcraft.research.frontend.integration.table

import cpw.mods.fml.common.network.IGuiHandler
import elan.tweaks.thaumcraft.research.frontend.integration.ThaumcraftResearchTweaks
import elan.tweaks.thaumcraft.research.frontend.integration.table.ThaumcraftResearchGuiHandler.IDs.RESEARCH_TABLE
import elan.tweaks.thaumcraft.research.frontend.integration.table.container.ResearchTableContainerFactory
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.ResearchTableGuiFactory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thaumcraft.common.tiles.TileResearchTable

class ThaumcraftResearchGuiHandler : IGuiHandler {

  private val logger: Logger = LogManager.getLogger(ThaumcraftResearchTweaks.MOD_ID)

  object IDs {
    const val RESEARCH_TABLE = 0
  }

  override fun getServerGuiElement(
      id: Int,
      player: EntityPlayer,
      world: World,
      x: Int,
      y: Int,
      z: Int
  ): Any? =
      if (id == RESEARCH_TABLE)
          ResearchTableContainerFactory.create(player.inventory, world.getTableTileEntity(x, y, z))
      else unknownGui(id)

  override fun getClientGuiElement(
      id: Int,
      player: EntityPlayer,
      world: World,
      x: Int,
      y: Int,
      z: Int
  ): Any? =
      if (id == RESEARCH_TABLE)
          ResearchTableGuiFactory.create(player, world.getTableTileEntity(x, y, z))
      else unknownGui(id)

  private fun unknownGui(id: Int): Any? {
    logger.error("Received unknown gui id: $id")
    return null
  }

  private fun World.getTableTileEntity(x: Int, y: Int, z: Int) =
      getTileEntity(x, y, z) as TileResearchTable
}
