package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.common.gui.ComposableContainerGui
import elan.tweaks.common.gui.component.UIComponent
import elan.tweaks.common.gui.component.texture.TextureBackgroundUIComponent.Companion.background
import elan.tweaks.common.gui.geometry.Rectangle
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.grid.Grid
import elan.tweaks.common.gui.geometry.grid.GridDynamicListAdapter
import elan.tweaks.thaumcraft.research.integration.client.container.ResearchTableContainerFactory
import elan.tweaks.thaumcraft.research.integration.client.gui.component.AspectDragAndDropUIComponent
import elan.tweaks.thaumcraft.research.integration.client.gui.component.AspectPool
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture.AspectPools
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import thaumcraft.api.aspects.Aspect
import thaumcraft.common.Thaumcraft
import thaumcraft.common.lib.network.PacketHandler
import thaumcraft.common.lib.network.playerdata.PacketAspectCombinationToServer
import thaumcraft.common.tiles.TileResearchTable

object ResearchTableGuiFactory {

    fun createFor(
        player: EntityPlayer,
        world: World, x: Int, y: Int, z: Int
    ): ComposableContainerGui {
        val tableEntity = world.getTableTileEntity(x, y, z)

        return ComposableContainerGui(
            createContainer(player, tableEntity),
            components = tableAndInventoryBackgrounds() + aspectPoolsOf(player, tableEntity) + AspectDragAndDropUIComponent(),
            xSize = ResearchTableInventoryTexture.width,
            ySize = ResearchTableInventoryTexture.inventoryOrigin.y + PlayerInventoryTexture.height
        )
    }

    private fun tableAndInventoryBackgrounds() = listOf(
        background(
            uiOrigin = Vector2D.ZERO,
            texture = ResearchTableInventoryTexture
        ),
        background(
            uiOrigin = ResearchTableInventoryTexture.inventoryOrigin,
            texture = PlayerInventoryTexture
        )
    )

    private fun aspectPoolsOf(
        player: EntityPlayer,
        tableEntity: TileResearchTable
    ): List<UIComponent> {
        val leftAspectPool = aspectPoolFor(
            player,
            tableEntity,
            AspectPools.leftRectangle
        ) { index, _ -> index % 2 == 0 }

        val rightAspectPool = aspectPoolFor(
            player,
            tableEntity,
            AspectPools.rightRectangle
        ) { index, _ -> index % 2 != 0 }
        
        return listOf(leftAspectPool, rightAspectPool)
    }

    private fun aspectPoolFor(
        player: EntityPlayer,
        tableEntity: TileResearchTable,
        bounds: Rectangle,
        aspectSelector: (Int, Aspect) -> Boolean
    ): AspectPool {
        val aspectPoolGrid: Grid<Aspect> = GridDynamicListAdapter(
            bounds = bounds,
            cellSize = AspectPools.ASPECT_CELL_SIZE_PIXEL
        ) {
            discoveredAspectsBy(player)
                .aspectsSorted
                .filterIndexed(aspectSelector)
        }
        
        return AspectPool(
            aspectPoolGrid,
            aspectAmountProvider(player),
            bonusAspectAmountProvider(tableEntity),
            aspectCombinationRequestSender(player, tableEntity)
        )
    }

    private fun aspectCombinationRequestSender(
        player: EntityPlayer,
        tableEntity: TileResearchTable
    ) = { firstAspect: Aspect, secondAspect: Aspect ->
        PacketHandler.INSTANCE.sendToServer(
            PacketAspectCombinationToServer(
                player,
                tableEntity.xCoord, tableEntity.yCoord, tableEntity.zCoord,
                firstAspect, secondAspect,
                tableEntity.bonusAspects.getAmount(firstAspect) > 0,
                tableEntity.bonusAspects.getAmount(secondAspect) > 0,
                true
            )
        )
    }

    private fun bonusAspectAmountProvider(tableEntity: TileResearchTable) = { aspect: Aspect ->
        tableEntity.bonusAspects.getAmount(aspect)
    }

    private fun aspectAmountProvider(player: EntityPlayer) = { aspect: Aspect ->
        discoveredAspectsBy(player).getAmount(aspect).toFloat()
    }

    private fun discoveredAspectsBy(player: EntityPlayer) =
        Thaumcraft.proxy.getPlayerKnowledge().getAspectsDiscovered(player.commandSenderName)


    fun createContainer(
        player: EntityPlayer,
        world: World, x: Int, y: Int, z: Int
    ) =
        createContainer(player, world.getTableTileEntity(x, y, z))

    private fun World.getTableTileEntity(x: Int, y: Int, z: Int) =
        getTileEntity(x, y, z) as TileResearchTable

    private fun createContainer(
        player: EntityPlayer,
        researchTableTileEntity: TileResearchTable
    ) =
        ResearchTableContainerFactory.create(
            player.inventory,
            researchTableTileEntity,
            scribeToolsSlotOrigin = ResearchTableInventoryTexture.Slots.scribeToolsOrigin,
            notesSlotOrigin = ResearchTableInventoryTexture.Slots.notesOrigin,
            inventorySlotOrigin = ResearchTableInventoryTexture.inventoryOrigin,
        )

}
