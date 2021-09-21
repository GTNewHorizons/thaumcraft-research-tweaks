package elan.tweaks.thaumcraft.research.integration.client.gui

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

    fun create(
        playerInventory: InventoryPlayer,
        tableTileEntity: TileResearchTable,
        scribeToolsSlotOffset: Vector = Vector(14, 10),
        notesSlotOffset: Vector = Vector(70, 10),
        inventorySlotOffset: Vector = Vector(48, 175)
    ) =
        SpecializedContainer(
            onEnchantItem = duplicateResearch(tableTileEntity),
            onCanInteractWith = tableTileEntity::isUseableByPlayer,
            specializedSlotIndexRange = specializedSlotIndexRange,
        ).apply {
            addSlotToContainer(
                SpecializedContainer.SpecializedSlot.specializedOn<IScribeTools>(
                    tableTileEntity,
                    SCRIBE_TOOLS_SLOT_INDEX,
                    scribeToolsSlotOffset.x,
                    scribeToolsSlotOffset.y
                )
            )
            addSlotToContainer(
                SpecializedContainer.SpecializedSlot.specializedOn<ItemResearchNotes>(
                    tableTileEntity,
                    RESEARCH_NOTES_SLOT_INDEX,
                    notesSlotOffset.x,
                    notesSlotOffset.y
                )
            )
            addPlayerInventorySlots(playerInventory, inventorySlotOffset)
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
        inventorySlotOffset: Vector
    ) {
        addInternalInventorySlots(playerInventory, inventorySlotOffset)
        addHobartSlots(playerInventory, inventorySlotOffset)
    }

    private fun SpecializedContainer.addInternalInventorySlots(
        playerInventory: InventoryPlayer,
        inventorySlotOffset: Vector
    ) {
        for (rowIndex in PlayerInventoryTexture.internalRowIndexes) {
            for (columnIndex in PlayerInventoryTexture.columnIndexes) {
                val inventoryIndex = columnIndex + rowIndex * PlayerInventoryTexture.SLOTS_IN_INVENTORY_ROW + PlayerInventoryTexture.SLOTS_IN_INVENTORY_ROW
                val x = inventorySlotOffset.x + columnIndex * PlayerInventoryTexture.SLOT_SIZE_PIXELS
                val y = inventorySlotOffset.y + rowIndex * PlayerInventoryTexture.SLOT_SIZE_PIXELS
                addSlotToContainer(Slot(playerInventory, inventoryIndex, x, y))
            }
        }
    }

    private fun SpecializedContainer.addHobartSlots(
        playerInventory: InventoryPlayer,
        inventorySlotOffset: Vector
    ) {
        for (columnIndex in PlayerInventoryTexture.columnIndexes) {
            val x = inventorySlotOffset.x + columnIndex * PlayerInventoryTexture.SLOT_SIZE_PIXELS
            val y = inventorySlotOffset.y + PlayerInventoryTexture.HOTBAR_ROW_INDEX * PlayerInventoryTexture.SLOT_SIZE_PIXELS + PlayerInventoryTexture.HOTBAR_ROW_DELIMITER_HEIGHT_PIXELS
            addSlotToContainer(Slot(playerInventory, columnIndex, x, y))
        }
    }
}
