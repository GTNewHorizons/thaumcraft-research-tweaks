package elan.tweaks.thaumcraft.research.integration.table.gui.textures

import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance

object PlayerInventoryTexture : ThaumcraftTextureInstance(
    "gui/guiresearchtable2.png", 
    textureWidth = 256,
    u = 0, v = 167,
    width = 176, height = 88
) {
    const val SLOT_SIZE_PIXELS = 18
    const val HOTBAR_ROW_DELIMITER_HEIGHT_PIXELS = 4

    const val SLOTS_IN_INVENTORY_ROW = 9
    val columnIndexes = 0..8
    val internalRowIndexes = 0..2 
    const val HOTBAR_ROW_INDEX = 3
}

