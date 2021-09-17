package elan.tweaks.thaumcraft.research.domain

import com.google.common.base.Preconditions

// There are actually couple of bounded contextes here:
// - aspect pallet & combiner
// - research item management (scribling tools, research paper & duplication?) 
// - player inventory?
// - actual research area

data class ResearchProcess(
    val researchExpertise: Boolean,
    val researchMastery: Boolean,
    val researchDuplication: Boolean,

    val diagram: ResearchHexDiagram,

    val inkSource: InkSource,

    val finished: Boolean = false
)


data class ResearchHexDiagram(
    val radius: UInt,
    val columns: List<Column>,
    private val mutablePaths: MutableList<Path>
) {
    init {
        Preconditions.checkArgument(radius > 1u, "Knowledge hex atlas radius ($radius) < 1")
    }

    val diameter get() = (2u * radius) - 1u
    val paths: List<Path> get() = mutablePaths

    data class Column(private val mutableNodes: MutableList<Node>) {
        val nodes: List<Node> get() = mutableNodes
    }

    data class Path(private val mutableNodes: MutableList<Node.Filled>) {
        val nodes: List<Node.Filled> get() = mutableNodes
    }
}


sealed class Node {
    abstract val columnIndex: UInt
    abstract val rowIndex: UInt

    data class Empty(
        override val columnIndex: UInt,
        override val rowIndex: UInt
    ) : Node()

    data class Filled(
        override val columnIndex: UInt,
        override val rowIndex: UInt,
        val aspectTag: String,
        val root: Boolean = false,
    ) : Node()

    data class Blocked(
        override val columnIndex: UInt,
        override val rowIndex: UInt
    ) : Node()
}

// API?

interface ResearchDiagramController {
    fun write(aspectTag: String, node: Node.Empty): Result<Unit>
    fun erase(node: Node.Filled): Result<Unit>
}

// SPI?

interface InkSource {
    fun isEmpty(): Boolean
    fun use(): Result<Unit>
}

// probably MPI

interface ResearchDiagramView {
    // I should get this init logic out of here to the place where I call back when I need to update the view
    fun init(atlas: ResearchHexDiagram): Result<Unit>
    
    fun place(node: Node.Filled): Result<Unit>
    fun connect(first: Node.Filled, second: Node.Filled): Result<Unit>
    fun disconnect(first: Node.Filled, second: Node.Filled): Result<Unit>
}

interface Research {
    fun add(node: Node.Filled): Result<Unit>

    // TODO: do we actually need or it will figure out connections and complete state by itself?
    fun complete(): Result<Unit>
    fun connect(first: Node.Filled, second: Node.Filled): Result<Unit>
    fun disconnect(first: Node.Filled, second: Node.Filled): Result<Unit>
}
