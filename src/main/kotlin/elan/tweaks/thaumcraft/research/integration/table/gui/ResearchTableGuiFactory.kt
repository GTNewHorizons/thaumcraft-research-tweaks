package elan.tweaks.thaumcraft.research.integration.table.gui

import elan.tweaks.common.gui.ComposableContainerGui
import elan.tweaks.common.gui.component.UIComponent
import elan.tweaks.common.gui.component.texture.TextureBackgroundUIComponent.Companion.background
import elan.tweaks.common.gui.geometry.Rectangle
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.layout.grid.GridLayout
import elan.tweaks.common.gui.layout.grid.GridLayoutDynamicListAdapter
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.domain.model.AspectPallet
import elan.tweaks.thaumcraft.research.domain.model.AspectsTree
import elan.tweaks.thaumcraft.research.domain.model.Research
import elan.tweaks.thaumcraft.research.domain.ports.provided.AspectPalletPort
import elan.tweaks.thaumcraft.research.domain.ports.provided.ResearchPort
import elan.tweaks.thaumcraft.research.domain.ports.required.KnowledgeBase
import elan.tweaks.thaumcraft.research.integration.adapters.*
import elan.tweaks.thaumcraft.research.integration.adapters.layout.AspectHex
import elan.tweaks.thaumcraft.research.integration.adapters.layout.HexLayoutResearchNoteDataAdapter
import elan.tweaks.thaumcraft.research.integration.table.container.ResearchTableContainerFactory
import elan.tweaks.thaumcraft.research.integration.table.gui.component.*
import elan.tweaks.thaumcraft.research.integration.table.gui.component.area.AspectHexMapEditorUIComponent
import elan.tweaks.thaumcraft.research.integration.table.gui.component.area.AspectHexMapUIComponent
import elan.tweaks.thaumcraft.research.integration.table.gui.component.area.ParchmentUIComponent
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ParchmentTexture
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ResearchTableInventoryTexture
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ResearchTableInventoryTexture.AspectPools
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ResearchTableInventoryTexture.CopyButton
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ResearchTableInventoryTexture.ResearchArea
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.api.aspects.Aspect
import thaumcraft.common.lib.research.ResearchManager
import thaumcraft.common.tiles.TileResearchTable

object ResearchTableGuiFactory {

    fun create(
        player: EntityPlayer,
        table: TileResearchTable
    ): ComposableContainerGui {
        val container = ResearchTableContainerFactory.create(
            player.inventory,
            table,
            scribeToolsSlotOrigin = ResearchTableInventoryTexture.Slots.scribeToolsOrigin,
            notesSlotOrigin = ResearchTableInventoryTexture.Slots.notesOrigin,
            inventorySlotOrigin = ResearchTableInventoryTexture.inventoryOrigin,
        )

        val research = Research(
            notes = ResearchNotesAdapter(player, table),
            pool = AspectPoolAdapter(player, table)
        )
        val knowledge = KnowledgeBaseAdapter(playerCommandSenderName = player.commandSenderName)

        val pallet = createPallet(player, table, knowledge)

        val scribeTools = ScribeToolsAdapter(table)

        return ComposableContainerGui(
            container,
            components =
            tableAndInventoryBackgrounds()
                    + researchArea(research, table)
                    + copyButton(research, pallet, knowledge)
                    + componentsOf(pallet)
                    + InkNotificationUIComponent(research, scribeTools, ResearchArea.centerOrigin)
                    + aspectDragAndDrop(pallet)
                    + KnowledgeNotificationUIComponent(),
            xSize = ResearchTableInventoryTexture.width,
            ySize = ResearchTableInventoryTexture.inventoryOrigin.y + PlayerInventoryTexture.height
        )
    }

    private fun copyButton(
        research: Research,
        pallet: AspectPallet,
        knowledge: KnowledgeBaseAdapter
    ) = CopyButtonUIComponent(
        bounds = CopyButton.bounds,
        requirementsUiOrigin = CopyButton.requirementsUiOrigin,
        research = research,
        aspectPallet = pallet,
        knowledge = knowledge
    )

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
    private fun researchArea(research: ResearchPort, table: TileResearchTable): Set<UIComponent> {
        val hexSize = 9

        val notesDataProvider = {
            ResearchManager.getData(
                table.getStackInSlot(ResearchTableContainerFactory.RESEARCH_NOTES_SLOT_INDEX)
            )
        }

        val hexLayout: HexLayout<AspectHex> = HexLayoutResearchNoteDataAdapter(
            bounds = ResearchArea.bounds,
            centerUiOrigin = ResearchArea.centerOrigin,
            aspectTree = AspectsTree(),
            hexSize = hexSize, // TODO: move to hex texture constants
            notesDataProvider = notesDataProvider
        )

        return setOf(
            ParchmentUIComponent(
                research,
                hexLayout,

                uiOrigin = ResearchArea.bounds.origin,
                runeLimit = 15,

                hexSize = hexSize,
                centerOffset = ParchmentTexture.centerOrigin
            ),
            AspectHexMapUIComponent(research, hexLayout),
            AspectHexMapEditorUIComponent(research, hexLayout)
        )
    }

    private fun createPallet(player: EntityPlayer, table: TileResearchTable, knowledge: KnowledgeBase) =
        AspectPallet(
            pool = AspectPoolAdapter(player, table),
            knowledge = knowledge,
            combiner = AspectCombinerAdapter(player, table)
        )

}
