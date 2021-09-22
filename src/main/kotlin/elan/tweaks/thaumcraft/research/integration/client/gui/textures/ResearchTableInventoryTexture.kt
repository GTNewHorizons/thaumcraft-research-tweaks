package elan.tweaks.thaumcraft.research.integration.client.gui.textures

import elan.tweaks.thaumcraft.research.integration.client.gui.UV
import elan.tweaks.thaumcraft.research.integration.client.gui.Vector

object ResearchTableInventoryTexture : ThaumcraftTextureInstance(
    "textures/research/table/research-table.png", 
    textureWidth = 332, textureHeight = 256,
    u = 0, v = 0,
    width = 332, height = 224
) {
    object Slots {
        const val SIZE_PIXELS = 18
        val scribeToolsOrigin = Vector(x = 86, y = 9)
        val notesOrigin = Vector(x = 110, y = 9)
    }

    object Buttons {
        const val SIZE_PIXELS = 18
        val copyOrigin = Vector(x = 134, y = 9)
        val copyActiveUV = UV(u = 2, v = 227)
    }

    val inventoryOrigin = Vector(x = 78, y = 201)
}

