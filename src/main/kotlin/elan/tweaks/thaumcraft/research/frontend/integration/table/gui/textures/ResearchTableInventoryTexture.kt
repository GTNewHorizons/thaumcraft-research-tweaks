package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Rectangle
import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance

object ResearchTableInventoryTexture : ThaumcraftTextureInstance(
    "research/table/research-table.png",
    textureScale = Scale(width = 310, height =  245),
    uvScale = Scale(width = 310, height =  219)
) {
    val inventoryOrigin = Vector2D(x = 67, y = 191)

    object Slots {
        val scribeToolsOrigin = Vector2D(x = 75, y = 10)
        val notesOrigin = Vector2D(x = 219, y = 10)
    }

    object AspectPools {
        const val ASPECT_CELL_SIZE_PIXEL = 16
        private const val COLUMNS = 3
        private const val ROWS = 12

        private val scale = Scale(width = COLUMNS * ASPECT_CELL_SIZE_PIXEL, height = ROWS * ASPECT_CELL_SIZE_PIXEL)

        val leftBound = Rectangle(
            origin = Vector2D(x = 12, y = 12),
            scale
        )

        val rightBound = Rectangle(
            origin = Vector2D(x = 250, y = 12),
            scale
        )
    }

    object ResearchArea {
        val bounds = Rectangle(
            origin = Vector2D(80, 35),
            scale = ParchmentTexture.scale
        )
        val centerOrigin = bounds.origin + ParchmentTexture.centerOrigin
    }

    object CopyButton {
        private const val SHADOW_PIXELS = 6
        private const val SIZE_PIXEL = 18
        const val SIZE_WITH_SHADOW_PIXELS = SIZE_PIXEL + SHADOW_PIXELS
        private val uiOrigin = Vector2D(x = 191, y = 6)
        val bounds = Rectangle(
            origin = uiOrigin,
            scale = Scale(SIZE_PIXEL, SIZE_PIXEL)
        )
        val requirementsUiOrigin = ResearchArea.bounds.origin + Vector2D(x = 6, y = 4)
    }
}
