package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.UV
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance

object PlayerInventoryTexture :
    ThaumcraftTextureInstance(
        "gui/guiresearchtable2.png",
        textureScale = Scale.cube(256),
        uv = UV(0, 167),
        uvScale = Scale(width = 176, height = 88)) {
  const val SLOT_SIZE_PIXELS = 18
  val internalRowIndexes = 0..2

  object SlotMapping {
    private val TOP_ROW = Vector2D(x = 8, y = 7) to 9..17
    private val MIDDLE_ROW = Vector2D(x = 8, y = 25) to 18..26
    private val BOTTOM_ROW = Vector2D(x = 8, y = 43) to 27..35
    private val HOTBAR_ROW = Vector2D(x = 8, y = 65) to 0..8

    private val rowOriginToSlotIndexes = setOf(TOP_ROW, MIDDLE_ROW, BOTTOM_ROW, HOTBAR_ROW)

    val mappings = slotUiMap()

    private fun slotUiMap() =
        rowOriginToSlotIndexes.flatMap { (origin, slotIndexes) ->
          slotIndexes.mapIndexed { columnIndex, slotIndex ->
            UIMapping(
                index = slotIndex,
                origin = origin.copy(x = origin.x + columnIndex * SLOT_SIZE_PIXELS))
          }
        }

    data class UIMapping(val index: Int, val origin: Vector2D)
  }
}
