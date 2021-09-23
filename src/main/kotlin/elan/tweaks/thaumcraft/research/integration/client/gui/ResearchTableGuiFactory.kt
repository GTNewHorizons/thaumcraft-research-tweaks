package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.thaumcraft.research.integration.client.container.ResearchTableContainerFactory
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import thaumcraft.common.tiles.TileResearchTable

object ResearchTableGuiFactory {

    fun createFor(
        player: EntityPlayer,
        world: World, x: Int, y: Int, z: Int
    ) =
        ResearchTableGui(createContainer(player, world, x, y, z))

    fun createContainer(
        player: EntityPlayer,
        world: World, x: Int, y: Int, z: Int
    ) =
        ResearchTableContainerFactory.create(
            player.inventory,
            world.getTileEntity(x, y, z) as TileResearchTable,
            scribeToolsSlotOrigin = ResearchTableInventoryTexture.Slots.scribeToolsOrigin,
            notesSlotOrigin = ResearchTableInventoryTexture.Slots.notesOrigin,
            inventorySlotOrigin =  ResearchTableInventoryTexture.inventoryOrigin,
        )

}
