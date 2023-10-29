package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Rectangle
import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance

object ResearchTableInventoryTexture :
    ThaumcraftTextureInstance(
        "research/table/research-table.png",
        textureScale = Scale(width = 342, height = 245),
        uvScale = Scale(width = 342, height = 219)) {
  val inventoryOrigin = Vector2D(x = 83, y = 191)

  object Slots {
    val scribeToolsOrigin = Vector2D(x = 91, y = 10)
    val notesOrigin = Vector2D(x = 235, y = 10)
  }

  object AspectPools {
    const val ASPECT_CELL_SIZE_PIXEL = 16
    private const val COLUMNS = 4
    private const val ROWS = 12

    private val scale =
        Scale(width = COLUMNS * ASPECT_CELL_SIZE_PIXEL, height = ROWS * ASPECT_CELL_SIZE_PIXEL)

    val leftBound = Rectangle(origin = Vector2D(x = 12, y = 12), scale)

    val rightBound = Rectangle(origin = Vector2D(x = 266, y = 12), scale)
  }

  object ResearchArea {
    val bounds = Rectangle(origin = Vector2D(96, 35), scale = ParchmentTexture.scale)
    val centerOrigin = bounds.origin + ParchmentTexture.centerOrigin
  }

  object CopyButton {
    private const val SHADOW_PIXELS = 6
    private const val SIZE_PIXEL = 18
    const val SIZE_WITH_SHADOW_PIXELS = SIZE_PIXEL + SHADOW_PIXELS
    private val uiOrigin = Vector2D(x = 207, y = 6)
    val bounds = Rectangle(origin = uiOrigin, scale = Scale.cube(SIZE_PIXEL))
    val requirementsUiOrigin = ResearchArea.bounds.origin + Vector2D(x = 6, y = 4)
  }

  object UsageHint {
    private const val SHADOW_PIXELS = 6
    private const val SIZE_PIXEL = 18
    const val SIZE_WITH_SHADOW_PIXELS = SIZE_PIXEL + SHADOW_PIXELS
    private val uiOrigin = Vector2D(x = 111, y = 6)
    val uiBounds = Rectangle(origin = uiOrigin, scale = Scale.cube(SIZE_WITH_SHADOW_PIXELS))
    private val onMouseOverOrigin = Vector2D(x = 114, y = 10)
    val onMouseOverBounds = Rectangle(origin = onMouseOverOrigin, scale = Scale.cube(SIZE_PIXEL))
  }
}
