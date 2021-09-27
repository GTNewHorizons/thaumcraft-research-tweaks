package elan.tweaks.thaumcraft.research.integration.client.container

import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import thaumcraft.api.IScribeTools
import thaumcraft.common.items.ItemResearchNotes
import thaumcraft.common.tiles.TileResearchTable

object ResearchTableContainerFactory {
    private const val SCRIBE_TOOLS_SLOT_INDEX = 0
    private const val RESEARCH_NOTES_SLOT_INDEX = 1
    private val specializedSlotIndexRange = SCRIBE_TOOLS_SLOT_INDEX..RESEARCH_NOTES_SLOT_INDEX
    private val inventorySlotOffset = Vector2D(8,7) // GuiContainer offsets inventory slots

    fun create(
        playerInventory: InventoryPlayer,
        tableTileEntity: TileResearchTable,
        scribeToolsSlotOrigin: Vector2D,
        notesSlotOrigin: Vector2D,
        inventorySlotOrigin: Vector2D,
    ) =
        SpecializedContainer(
            onEnchantItem = duplicateResearch(tableTileEntity),
            onCanInteractWith = tableTileEntity::isUseableByPlayer,
            specializedSlotIndexRange = specializedSlotIndexRange,
        ).apply {
            addResearchTableSlots(tableTileEntity, scribeToolsSlotOrigin, notesSlotOrigin)
            addPlayerInventorySlots(playerInventory, inventorySlotOrigin + inventorySlotOffset)
        }

    private fun SpecializedContainer.addResearchTableSlots(
        tableTileEntity: TileResearchTable,
        scribeToolsSlotOrigin: Vector2D,
        notesSlotOrigin: Vector2D
    ) {
        addSlotToContainer(
            SpecializedContainer.SpecializedSlot.specializedOn<IScribeTools>(
                tableTileEntity,
                SCRIBE_TOOLS_SLOT_INDEX,
                scribeToolsSlotOrigin.x,
                scribeToolsSlotOrigin.y
            )
        )
        addSlotToContainer(
            SpecializedContainer.SpecializedSlot.specializedOn<ItemResearchNotes>(
                tableTileEntity,
                RESEARCH_NOTES_SLOT_INDEX,
                notesSlotOrigin.x,
                notesSlotOrigin.y
            )
        )
    }

    private fun duplicateResearch(tileEntity: TileResearchTable) = { player: EntityPlayer, button: Int ->
        when (button) {
            1 -> true
            5 -> {
                tileEntity.duplicate(player);
                true
            }
            else -> false
        }
    }

    private fun SpecializedContainer.addPlayerInventorySlots(
        playerInventory: InventoryPlayer,
        inventorySlotOffset: Vector2D
    ) {
        addHobartSlots(playerInventory, inventorySlotOffset)
        addInternalInventorySlots(playerInventory, inventorySlotOffset)
    }

    private fun SpecializedContainer.addHobartSlots(
        playerInventory: InventoryPlayer,
        inventorySlotOffset: Vector2D
    ) {
        for (columnIndex in PlayerInventoryTexture.columnIndexes) {
            val x = inventorySlotOffset.x + columnIndex * PlayerInventoryTexture.SLOT_SIZE_PIXELS
            val y =
                inventorySlotOffset.y + PlayerInventoryTexture.HOTBAR_ROW_INDEX * PlayerInventoryTexture.SLOT_SIZE_PIXELS + PlayerInventoryTexture.HOTBAR_ROW_DELIMITER_HEIGHT_PIXELS
            addSlotToContainer(Slot(playerInventory, columnIndex, x, y))
        }
    }

    private fun SpecializedContainer.addInternalInventorySlots(
        playerInventory: InventoryPlayer,
        inventorySlotOffset: Vector2D
    ) {
        val hotbarSlots = PlayerInventoryTexture.SLOTS_IN_INVENTORY_ROW
        for (rowIndex in PlayerInventoryTexture.internalRowIndexes) {
            for (columnIndex in PlayerInventoryTexture.columnIndexes) {
                val inventoryIndex = hotbarSlots + columnIndex + rowIndex * PlayerInventoryTexture.SLOTS_IN_INVENTORY_ROW
                val x = inventorySlotOffset.x + columnIndex * PlayerInventoryTexture.SLOT_SIZE_PIXELS
                val y = inventorySlotOffset.y + rowIndex * PlayerInventoryTexture.SLOT_SIZE_PIXELS
                addSlotToContainer(Slot(playerInventory, inventoryIndex, x, y))
            }
        }
    }
}
