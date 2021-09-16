package elan.tweaks.thaumcraft.research.domain

import com.google.common.base.Preconditions

data class ResearchProcess(
    val researchExpertise: Boolean,
    val researchMastery: Boolean,
    val researchDuplication: Boolean,

    val atlas: KnowledgeHexAtlas,

    val inkSource: InkSource,

    val finished: Boolean = false
)


data class KnowledgeHexAtlas(
    val radius: UInt,
    val hexColumns: List<MutableList<Node>>
) {
    init {
        Preconditions.checkArgument(radius > 1u, "Knowledge hex atlas radius ($radius) < 1")
    }

    val diameter get() = (2u * radius) - 1u
}

data class HexColumn(
    private val nodes: MutableList<Node>
){
    fun viewNodes(): List<Node> = nodes
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

class KnowledgePath(
    private val nodes: MutableList<Node.Filled>
) {
    fun viewNodes(): List<Node.Filled> = nodes
}

// API?

interface ResearchNotesController {
    fun write(aspectTag: String, node: Node.Empty): Result<Unit>
    fun erase(node: Node.Filled): Result<Unit>
}

// SPI?

interface InkSource {
    fun isEmpty(): Boolean
    fun use(): Result<Unit>
}

interface AspectKnowledgePool {
    fun hasAvailable(aspectTag: String): Boolean
    fun regain(aspectTag: String): Result<Unit>
    fun use(aspectTag: String): Result<Unit>
}

interface ResearchNotesView {
    // I should get this init logic out of here to the place where I call back when I need to update the view
    fun init(hexColumns: List<HexColumn>, paths: List<KnowledgePath>): Result<Unit>
    
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
