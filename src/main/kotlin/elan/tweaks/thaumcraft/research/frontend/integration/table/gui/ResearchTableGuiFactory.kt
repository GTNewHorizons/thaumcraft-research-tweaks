package elan.tweaks.thaumcraft.research.frontend.integration.table.gui

import elan.tweaks.common.gui.ComposableContainerGui.Companion.gui
import elan.tweaks.common.gui.component.UIComponent
import elan.tweaks.common.gui.component.texture.TextureBackgroundUIComponent.Companion.background
import elan.tweaks.common.gui.geometry.Rectangle
import elan.tweaks.common.gui.geometry.Scale
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.layout.grid.GridLayout
import elan.tweaks.common.gui.layout.grid.GridLayoutDynamicListAdapter
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.frontend.domain.model.AspectTree
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectPalletPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort
import elan.tweaks.thaumcraft.research.frontend.integration.adapters.layout.AspectHex
import elan.tweaks.thaumcraft.research.frontend.integration.adapters.layout.HexLayoutResearchNoteDataAdapter
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component.*
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component.area.AspectHexMapEditorUIComponent
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component.area.AspectHexMapUIComponent
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component.area.ParchmentUIComponent
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ParchmentTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ResearchTableInventoryTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ResearchTableInventoryTexture.AspectPools
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ResearchTableInventoryTexture.CopyButton
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ResearchTableInventoryTexture.ResearchArea
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.api.aspects.Aspect
import thaumcraft.common.tiles.TileResearchTable

object ResearchTableGuiFactory {

    private val guiScale = Scale(
        width = ResearchTableInventoryTexture.width,
        height = ResearchTableInventoryTexture.inventoryOrigin.y + PlayerInventoryTexture.height
    )

    fun create(
        player: EntityPlayer,
        table: TileResearchTable
    ) = PortContainer(player, table).run {
        gui(
            scale = guiScale,
            container = inventory,
            components =
            tableAndInventoryBackgrounds()
                    + researchArea(research, researcher)
                    + copyButton(research, researcher)
                    + palletComponents(pallet, researcher)
                    + ScribeToolsNotificationUIComponent(research, ResearchArea.centerOrigin)
                    + AspectDragAndDropUIComponent(pallet)
                    + KnowledgeNotificationUIComponent()
        )
    }

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

    private fun researchArea(research: ResearchProcessPort, researcher: ResearcherKnowledgePort): Set<UIComponent> {
        val hexSize = 9 // TODO: move to hex texture constants

        val hexLayout: HexLayout<AspectHex> = HexLayoutResearchNoteDataAdapter(
            bounds = ResearchArea.bounds,
            centerUiOrigin = ResearchArea.centerOrigin,
            aspectTree = AspectTree(),
            hexSize = hexSize,
            researcher = researcher,
            researchProcess = research
        )

        return setOf(
            ParchmentUIComponent(
                research = research,
                uiOrigin = ResearchArea.bounds.origin,

                runeLimit = 15,

                hexSize = hexSize,
                centerOffset = ParchmentTexture.centerOrigin,
                hexLayout = hexLayout,
            ),
            AspectHexMapUIComponent(research, hexLayout),
            AspectHexMapEditorUIComponent(research, hexLayout)
        )
    }

    private fun copyButton(research: ResearchProcessPort, researcher: ResearcherKnowledgePort) = CopyButtonUIComponent(
        bounds = CopyButton.bounds, requirementsUiOrigin = CopyButton.requirementsUiOrigin,
        research = research, researcher = researcher
    )

    private fun palletComponents(pallet: AspectPalletPort, researcher: ResearcherKnowledgePort): List<UIComponent> {
        val leftAspectPallet = palletComponent(
            pallet = pallet,
            researcher = researcher,
            bounds = AspectPools.leftBound
        ) { index, _ -> index % 2 == 0 } // TODO: split based on affinity

        val rightAspectPallet = palletComponent(
            pallet = pallet,
            researcher = researcher,
            bounds = AspectPools.rightBound
        ) { index, _ -> index % 2 != 0 } // TODO: split based on affinity

        return listOf(leftAspectPallet, rightAspectPallet)
    }

    private fun palletComponent(
        researcher: ResearcherKnowledgePort,
        pallet: AspectPalletPort,
        bounds: Rectangle,
        aspectSelector: (Int, Aspect) -> Boolean
    ): AspectPalletUIComponent {
        val aspectPalletGrid: GridLayout<Aspect> = GridLayoutDynamicListAdapter(
            bounds = bounds,
            cellSize = AspectPools.ASPECT_CELL_SIZE_PIXEL
        ) {
            researcher.allDiscoveredAspects()
                // TODO: add sorting
                .filterIndexed(aspectSelector)
        }

        return AspectPalletUIComponent(
            aspectPalletGrid,
            pallet,
        )
    }

}
