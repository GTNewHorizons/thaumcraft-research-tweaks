package elan.tweaks.thaumcraft.research.integration.table.gui

import elan.tweaks.common.gui.ComposableContainerGui
import elan.tweaks.common.gui.component.UIComponent
import elan.tweaks.common.gui.component.texture.TextureBackgroundUIComponent.Companion.background
import elan.tweaks.common.gui.geometry.Rectangle
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.layout.grid.GridLayout
import elan.tweaks.common.gui.layout.grid.GridLayoutDynamicListAdapter
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.domain.model.AspectsTree
import elan.tweaks.thaumcraft.research.domain.model.Research
import elan.tweaks.thaumcraft.research.domain.ports.provided.AspectPalletPort
import elan.tweaks.thaumcraft.research.integration.adapters.AspectPoolAdapter
import elan.tweaks.thaumcraft.research.integration.adapters.ResearchNotesAdapter
import elan.tweaks.thaumcraft.research.integration.adapters.layout.AspectHex
import elan.tweaks.thaumcraft.research.integration.adapters.layout.HexLayoutResearchNoteDataAdapter
import elan.tweaks.thaumcraft.research.integration.table.container.ResearchTableContainerFactory
import elan.tweaks.thaumcraft.research.integration.table.gui.component.AspectDragAndDropUIComponent
import elan.tweaks.thaumcraft.research.integration.table.gui.component.AspectPalletUIComponent
import elan.tweaks.thaumcraft.research.integration.table.gui.component.ResearchAreaUIComponent
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ResearchTableInventoryTexture
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ResearchTableInventoryTexture.AspectPools
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ResearchTableInventoryTexture.ResearchArea
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.api.aspects.Aspect
import thaumcraft.common.lib.research.ResearchManager
import thaumcraft.common.tiles.TileResearchTable

object ResearchTableGuiFactory {

    fun create(
        player: EntityPlayer,
        pallet: AspectPalletPort,
        table: TileResearchTable
    ): ComposableContainerGui {
        val container = ResearchTableContainerFactory.create(
            player.inventory,
            table,
            scribeToolsSlotOrigin = ResearchTableInventoryTexture.Slots.scribeToolsOrigin,
            notesSlotOrigin = ResearchTableInventoryTexture.Slots.notesOrigin,
            inventorySlotOrigin = ResearchTableInventoryTexture.inventoryOrigin,
        )

        return ComposableContainerGui(
            container,
            components = 
                    tableAndInventoryBackgrounds() 
                            + componentsOf(pallet) 
                            + researchArea(player, table)
                            + aspectDragAndDrop(pallet),
            xSize = ResearchTableInventoryTexture.width,
            ySize = ResearchTableInventoryTexture.inventoryOrigin.y + PlayerInventoryTexture.height
        )
    }

    private fun aspectDragAndDrop(pallet: AspectPalletPort) = 
        AspectDragAndDropUIComponent(
            pallet,
        )

    private fun tableAndInventoryBackgrounds() = listOf(
        background(
            uiOrigin = Vector2D.ZERO,
            texture = ResearchTableInventoryTexture,
        ),
        background(
            uiOrigin = ResearchTableInventoryTexture.inventoryOrigin,
            texture = PlayerInventoryTexture,
        )
    )

    private fun componentsOf(pallet: AspectPalletPort): List<UIComponent> {
        val leftAspectPallet = componentOf(
            pallet = pallet,
            bounds = AspectPools.leftBound
        ) { index, _ -> index % 2 == 0 } // TODO: split based on affinity

        val rightAspectPallet = componentOf(
            pallet = pallet,
            bounds = AspectPools.rightBound
        ) { index, _ -> index % 2 != 0 } // TODO: split based on affinity

        return listOf(leftAspectPallet, rightAspectPallet)
    }

    private fun componentOf(
        pallet: AspectPalletPort,
        bounds: Rectangle,
        aspectSelector: (Int, Aspect) -> Boolean
    ): AspectPalletUIComponent {
        val aspectPalletGrid: GridLayout<Aspect> = GridLayoutDynamicListAdapter(
            bounds = bounds,
            cellSize = AspectPools.ASPECT_CELL_SIZE_PIXEL
        ) {
            pallet.discoveredAspects()
                // TODO: add sorting
                .filterIndexed(aspectSelector)
        }

        return AspectPalletUIComponent(
            aspectPalletGrid,
            pallet,
        )
    }

    // TODO: move to factory
    private fun researchArea(player: EntityPlayer, table: TileResearchTable): UIComponent {
        val notesDataProvider = {
            ResearchManager.getData(
                table.getStackInSlot(ResearchTableContainerFactory.RESEARCH_NOTES_SLOT_INDEX)
            )
        }
        val area = Research(
            notes = ResearchNotesAdapter(player, table, notesDataProvider),
            pool = AspectPoolAdapter(player, table)
        )

        val hexLayout: HexLayout<AspectHex> = HexLayoutResearchNoteDataAdapter(
            bounds = ResearchArea.bounds,
            centerUiOrigin = ResearchArea.centerOrigin,
            aspectTree = AspectsTree(),
            hexSize = 9, // TODO: move to hex texture constants
            notesDataProvider = notesDataProvider
        )

        return ResearchAreaUIComponent(
            area,
            hexLayout,
            uiOrigin = ResearchArea.bounds.origin
        )
    }

}
