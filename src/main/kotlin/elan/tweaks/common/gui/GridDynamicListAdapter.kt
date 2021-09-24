package elan.tweaks.common.gui

class GridDynamicListAdapter<ElementT>(
    private val bounds: Rectangle,
    private val cellSize: Int,
    private val provideElements: () -> List<ElementT>
) : Grid<ElementT> {
    
    private val elements: List<ElementT>
        get() = provideElements()

    private val dimensions: Vector =
        Vector(bounds.scale.width / cellSize, bounds.scale.height / cellSize)

    override fun contains(uiPoint: Vector): Boolean = uiPoint in bounds

    /**
     * @point relative to ui origin (top left corner)
     *
     * @return element if present
     */
    override operator fun get(uiPoint: Vector): ElementT? {
        if (uiPoint !in bounds) {
            return null
        }

        val elementIndex = deduceIndex(uiPoint)

        return if (elementIndex in elements.indices)
            elements[elementIndex]
        else
            null
    }

    private fun deduceIndex(uiPoint: Vector): Int {
        val point = uiPoint - bounds.origin
        val columnIndex = (point.x / cellSize).coerceAtMost(dimensions.x - 1)
        val rowIndex = (point.y / cellSize).coerceAtMost(dimensions.y - 1)
        return columnIndex + rowIndex * dimensions.x
    }

    /**
     * @return Sequence over elements and their origin relative to ui origin (top left corner)
     */
    override fun asOriginSequence(): Sequence<Pair<Vector, ElementT>> =
        elements
            .asSequence()
            .mapIndexed { index: Int, element: ElementT ->
                toCellOrigin(index) to element
            }

    private fun toCellOrigin(index: Int) =
        Vector(
            x = (index % dimensions.x) * cellSize,
            y = (index / dimensions.x) * cellSize
        ) + bounds.origin

}
