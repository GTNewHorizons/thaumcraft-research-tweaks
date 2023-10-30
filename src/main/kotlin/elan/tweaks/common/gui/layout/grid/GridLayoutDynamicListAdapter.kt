package elan.tweaks.common.gui.layout.grid

import elan.tweaks.common.gui.dto.Rectangle
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.dto.VectorXY

class GridLayoutDynamicListAdapter<ElementT>(
    private val bounds: Rectangle,
    private val cellSize: Int,
    private val provideElements: () -> List<ElementT>
) : GridLayout<ElementT> {

  private val elements: List<ElementT>
    get() = provideElements()

  private val dimensions: Vector2D =
      Vector2D(bounds.scale.width / cellSize, bounds.scale.height / cellSize)

  override fun contains(uiPoint: VectorXY): Boolean = uiPoint in bounds

  /**
   * @point relative to ui origin (top left corner)
   *
   * @return element if present
   */
  override operator fun get(uiPoint: VectorXY): ElementT? {
    if (uiPoint !in bounds) return null

    val elementIndex = deduceIndex(uiPoint)

    return if (elementIndex in elements.indices) elements[elementIndex] else null
  }

  private fun deduceIndex(uiPoint: VectorXY): Int {
    val point = uiPoint - bounds.origin
    val columnIndex = (point.x / cellSize).coerceAtMost(dimensions.x - 1)
    val rowIndex = (point.y / cellSize).coerceAtMost(dimensions.y - 1)
    return columnIndex + rowIndex * dimensions.x
  }

  override fun asOriginList(): List<Pair<Vector2D, ElementT>> =
      elements.mapIndexed { index: Int, element: ElementT -> toCellOrigin(index) to element }

  private fun toCellOrigin(index: Int) =
      Vector2D(x = (index % dimensions.x) * cellSize, y = (index / dimensions.x) * cellSize) +
          bounds.origin
}
