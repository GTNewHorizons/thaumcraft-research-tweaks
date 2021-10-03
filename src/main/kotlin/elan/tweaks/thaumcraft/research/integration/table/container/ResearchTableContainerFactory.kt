package elan.tweaks.thaumcraft.research.integration.table.container

import elan.tweaks.common.container.SpecializedContainer
import elan.tweaks.common.container.SpecializedContainer.SpecializedSlot.Companion.specializedOn
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.PlayerInventoryTexture
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import thaumcraft.api.IScribeTools
import thaumcraft.common.items.ItemResearchNotes
import thaumcraft.common.tiles.TileResearchTable

object ResearchTableContainerFactory {
    // TODO: is this a layout constants too?
    const val SCRIBE_TOOLS_SLOT_INDEX = 0
    const val RESEARCH_NOTES_SLOT_INDEX = 1

    const val ENCHANT_ACTION_ID = 5

    private val specializedSlotIndexRange = SCRIBE_TOOLS_SLOT_INDEX..RESEARCH_NOTES_SLOT_INDEX

    fun create(
        playerInventory: InventoryPlayer,
        tableTileEntity: TileResearchTable,
        scribeToolsSlotOrigin: Vector2D = Vector2D.ZERO,
        notesSlotOrigin: Vector2D = Vector2D.ZERO,
        inventoryUiOrigin: Vector2D = Vector2D.ZERO,
    ) =
        SpecializedContainer(
            onEnchantItem = duplicateResearch(tableTileEntity),
            onCanInteractWith = tableTileEntity::isUseableByPlayer,
            specializedSlotIndexRange = specializedSlotIndexRange,
        ).apply {
            addResearchTableSlots(tableTileEntity, scribeToolsSlotOrigin, notesSlotOrigin)
            addPlayerInventorySlots(playerInventory, inventoryUiOrigin)
        }

    private fun SpecializedContainer.addResearchTableSlots(
        tableTileEntity: TileResearchTable,
        scribeToolsSlotOrigin: Vector2D,
        notesSlotOrigin: Vector2D
    ) {
        addSlotToContainer(
            specializedOn<IScribeTools>(tableTileEntity, SCRIBE_TOOLS_SLOT_INDEX, scribeToolsSlotOrigin)
        )
        addSlotToContainer(
            specializedOn<ItemResearchNotes>(tableTileEntity, RESEARCH_NOTES_SLOT_INDEX, notesSlotOrigin)
        )
    }

    private fun duplicateResearch(tileEntity: TileResearchTable) = { player: EntityPlayer, button: Int ->
        when (button) {
            1 -> true
            ENCHANT_ACTION_ID -> {
                tileEntity.duplicate(player)
                true
            }
            else -> false
        }
    }

    private fun SpecializedContainer.addPlayerInventorySlots(
        playerInventory: InventoryPlayer,
        inventoryUiOrigin: Vector2D
    ) {
        PlayerInventoryTexture.SlotMapping.mappings.forEach { mapping ->
            val origin = inventoryUiOrigin + mapping.origin
            addSlotToContainer(Slot(playerInventory, mapping.index, origin.x, origin.y))
        }
    }

}
