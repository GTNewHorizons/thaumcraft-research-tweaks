package elan.tweaks.thaumcraft.research.integration.client.gui.textures

import elan.tweaks.thaumcraft.research.integration.client.gui.Scale
import elan.tweaks.thaumcraft.research.integration.client.gui.UV
import elan.tweaks.thaumcraft.research.integration.client.gui.Vector

object ResearchTableInventoryTexture : ThaumcraftTextureInstance(
    "textures/research/table/research-table.png",
    textureWidth = 300, textureHeight = 256,
    u = 0, v = 0,
    width = 300, height = 222
) {
    object Slots {
        const val SIZE_PIXELS = 18
        val scribeToolsOrigin = Vector(x = 70, y = 9)
        val notesOrigin = Vector(x = 94, y = 9)
    }

    object Buttons {
        const val SIZE_PIXELS = 18
        val copyOrigin = Vector(x = 117, y = 8)
        val copyActiveUV = UV(u = 2, v = 227)
    }

    object AspectPools {
        const val ASPECT_SIZE_PIXEL = 16
        const val COLUMNS = 3
        const val ROWS = 12

        val leftOrigin = Vector(7, 24)
        val rightOrigin = Vector(245, 24)
        val scale = Scale(width = COLUMNS * ASPECT_SIZE_PIXEL, height = ROWS * ASPECT_SIZE_PIXEL)

    }

    val inventoryOrigin = Vector(x = 62, y = 198)
}

