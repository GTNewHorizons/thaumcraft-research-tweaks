package elan.tweaks.thaumcraft.research.frontend.integration.table.container

import elan.tweaks.common.container.SpecializedContainer
import elan.tweaks.common.container.SpecializedContainer.SpecializedSlot.Companion.specializedOn
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.PlayerInventoryTexture
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import thaumcraft.api.IScribeTools
import thaumcraft.common.items.ItemResearchNotes
import thaumcraft.common.tiles.TileResearchTable

object ResearchTableContainerFactory {
  const val SCRIBE_TOOLS_SLOT_INDEX = 0
  const val RESEARCH_NOTES_SLOT_INDEX = 1

  const val DUPLICATE_ACTION_ID = 5

  private val specializedSlotIndexRange = SCRIBE_TOOLS_SLOT_INDEX..RESEARCH_NOTES_SLOT_INDEX

  fun create(
      playerInventory: InventoryPlayer,
      table: TileResearchTable,
      scribeToolsSlotOrigin: Vector2D = Vector2D.ZERO,
      notesSlotOrigin: Vector2D = Vector2D.ZERO,
      inventoryUiOrigin: Vector2D = Vector2D.ZERO,
  ) =
      SpecializedContainer(
              onEnchantItem = duplicateResearch(table),
              onCanInteractWith = table::isUseableByPlayer,
              specializedSlotIndexRange = specializedSlotIndexRange,
          )
          .apply {
            addResearchTableSlots(table, scribeToolsSlotOrigin, notesSlotOrigin)
            addPlayerInventorySlots(playerInventory, inventoryUiOrigin)
          }

  private fun SpecializedContainer.addResearchTableSlots(
      tableTileEntity: TileResearchTable,
      scribeToolsSlotOrigin: Vector2D,
      notesSlotOrigin: Vector2D
  ) {
    addSlotToContainer(
        specializedOn<IScribeTools>(
            tableTileEntity, SCRIBE_TOOLS_SLOT_INDEX, scribeToolsSlotOrigin))
    addSlotToContainer(
        specializedOn<ItemResearchNotes>(
            tableTileEntity, RESEARCH_NOTES_SLOT_INDEX, notesSlotOrigin))
  }

  private fun duplicateResearch(tileEntity: TileResearchTable) =
      { player: EntityPlayer, actionId: Int ->
        when (actionId) {
          1 -> true
          DUPLICATE_ACTION_ID -> {
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
