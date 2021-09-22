package elan.tweaks.thaumcraft.research.integration.client.gui.textures

object PlayerInventoryTexture : ThaumcraftTextureInstance(
    "textures/gui/guiresearchtable2.png", textureWidth = 256,
    u = 0, v = 167,
    width = 176, height = 88
) {
    const val SLOTS_IN_INVENTORY_ROW = 9
    const val SLOT_SIZE_PIXELS = 18
    const val HOTBAR_ROW_DELIMITER_HEIGHT_PIXELS = 4

    val internalRowIndexes = 0..2 
    val columnIndexes = 0..8
    const val HOTBAR_ROW_INDEX = 3
    
}

