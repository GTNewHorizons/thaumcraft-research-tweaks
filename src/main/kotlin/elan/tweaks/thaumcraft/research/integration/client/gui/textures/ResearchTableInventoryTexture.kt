package elan.tweaks.thaumcraft.research.integration.client.gui.textures

import elan.tweaks.common.gui.Rectangle
import elan.tweaks.common.gui.Scale
import elan.tweaks.common.gui.UV
import elan.tweaks.common.gui.Vector

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
        const val ASPECT_CELL_SIZE_PIXEL = 16
        const val COLUMNS = 3
        const val ROWS = 12

        val leftOrigin = Vector(7, 24)
        val rightOrigin = Vector(245, 24)
        private val scale = Scale(width = COLUMNS * ASPECT_CELL_SIZE_PIXEL, height = ROWS * ASPECT_CELL_SIZE_PIXEL)

        val leftRectangle = Rectangle(
            leftOrigin,
            scale
        )
        
        val rightRectangle = Rectangle(
            rightOrigin,
            scale
        )
    }

    val inventoryOrigin = Vector(x = 62, y = 198)
}

