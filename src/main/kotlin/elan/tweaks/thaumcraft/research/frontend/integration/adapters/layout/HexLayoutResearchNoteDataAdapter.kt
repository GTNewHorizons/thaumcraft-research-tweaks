package elan.tweaks.thaumcraft.research.frontend.integration.adapters.layout

import elan.tweaks.common.gui.dto.HexVector
import elan.tweaks.common.gui.dto.Rectangle
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectsTreePort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.HexTexture
import thaumcraft.common.lib.research.ResearchManager
import thaumcraft.common.lib.utils.HexUtils
import java.util.*
import kotlin.math.roundToInt

class HexLayoutResearchNoteDataAdapter(
    private val bounds: Rectangle, // TODO: generalize bounds to origin and contains operator and use circular bounds here
    private val centerUiOrigin: VectorXY,
    private val aspectTree: AspectsTreePort,
    private val researcher: ResearcherKnowledgePort,
    private val researchProcess: ResearchProcessPort
) : HexLayout<AspectHex> {
    private val keyToAspectHex get() = keyToAspectHex()

    override fun contains(uiPoint: VectorXY): Boolean {
        if (uiPoint !in bounds) return false

        val hexKey = uiPoint.toHexKey()
        return hexPresent(hexKey)
    }

    override fun get(uiPoint: VectorXY): AspectHex? {
        if (uiPoint !in bounds) return null

        val hexKey = uiPoint.toHexKey()
        return keyToAspectHex[hexKey]
    }

    private fun VectorXY.toHexKey(): String =
        HexVector.roundedFrom(this - centerUiOrigin, HexTexture.SIZE_PIXELS).key

    override fun asOriginList(): List<Pair<VectorXY, AspectHex>> =
        keyToAspectHex.values
            .map { aspectHex -> aspectHex.uiOrigin to aspectHex }

    // TODO extract this to separate component, which would probably also handle note data provision and computation caching
    private fun keyToAspectHex(): Map<String, AspectHex> {
        val hexEntries = getHexEntries()
        val hexes = getHexes()

        val (traversedKeys, keyToNeighbourKeys) = traversRootPathsAndBuildConnectionMap(hexEntries, hexes)

        return hexEntries
            .mapValues { (key, entry) ->
                entry.convertToAspectHex(key, hexes, keyToNeighbourKeys, traversedKeys)
            }
            .toMap()
    }

    private fun ResearchManager.HexEntry.convertToAspectHex(
        key: String,
        hexes: Map<String, HexUtils.Hex>,
        keyToNeighbourKeys: Map<String, Set<String>>,
        traversedKeys: Set<String>
    ): AspectHex {
        val uiCenter = hexes.getUiCenter(key)
        val uiOrigin = HexTexture.toOrigin(uiCenter)
        val connections =
            keyToNeighbourKeys
                .getOrDefault(key, emptySet())
                .map { neighbourKey -> hexes.getUiCenter(neighbourKey) }
                .toSet()

        return when (type) {
            HexType.ROOT -> AspectHex.Occupied.Root(
                uiOrigin = uiOrigin,
                uiCenter = uiCenter,
                aspect = aspect,
                connectionTargetsCenters = connections
            )
            HexType.NODE -> AspectHex.Occupied.Node(
                key = key,
                uiOrigin = uiOrigin,
                uiCenter = uiCenter,
                aspect = aspect,
                onRootPath = key in traversedKeys,
                connectionTargetsCenters = connections
            )
            else -> AspectHex.Vacant(
                key = key,
                uiOrigin = uiOrigin,
            )
        }
    }

    private fun Map<String, HexUtils.Hex>.getUiCenter(key: String) =
        getValue(key).origin + centerUiOrigin


    private fun traversRootPathsAndBuildConnectionMap(
        hexEntries: Map<String, ResearchManager.HexEntry>,
        hexes: Map<String, HexUtils.Hex>
    ): Pair<Set<String>, Map<String, Set<String>>> {
        val rootHexes = hexEntries.filterValues { entry -> entry.type == HexType.ROOT }.keys

        val traversedKeys = mutableSetOf<String>()
        val relatedNodeKeys = mutableMapOf<String, Set<String>>()
        val keysToTraverse = Stack<String>()

        keysToTraverse += rootHexes

        val discoveredAspects = researcher.allDiscoveredAspects().toSet()

        while (keysToTraverse.isNotEmpty()) {
            val key = keysToTraverse.pop()
            val entry = hexEntries[key] ?: continue
            val hex = hexes[key] ?: continue
            if (key in traversedKeys || entry.aspect !in discoveredAspects) continue

            val newRelatedNeighbors =
                (0..5)
                    .map { neighborIndex -> hex.neighborKey(neighborIndex) }
                    .filter(this::hexPresent)
                    .filter { neighbourKey ->
                        val neighborEntry = hexEntries[neighbourKey]!!
                        neighbourKey !in traversedKeys
                                && neighborEntry.type != HexType.VACANT
                                && neighborEntry.aspect in discoveredAspects
                                && aspectTree.areRelated(entry.aspect, neighborEntry.aspect)
                    }.toSet()

            keysToTraverse += newRelatedNeighbors
            relatedNodeKeys[key] = newRelatedNeighbors
            traversedKeys += key
        }
        return Pair(traversedKeys, relatedNodeKeys)
    }

    private fun hexPresent(hexKey: String) =
        getHexEntries().containsKey(hexKey) && getHexes().containsKey(hexKey)

    private fun getHexEntries() = researchProcess.data?.hexEntries ?: emptyMap()
    private fun getHexes() = researchProcess.data?.hexes ?: emptyMap()

    private fun HexUtils.Hex.neighborKey(index: Int) = getNeighbour(index).toString()

    private val HexUtils.Hex.origin
        get() = toPixel(HexTexture.SIZE_PIXELS)
            .run { Vector2D(x.roundToInt(), y.roundToInt()) }

    private object HexType {
        const val VACANT = 0
        const val ROOT = 1
        const val NODE = 2
    }
}
