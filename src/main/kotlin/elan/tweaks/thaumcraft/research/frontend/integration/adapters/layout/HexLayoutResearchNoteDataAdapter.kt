package elan.tweaks.thaumcraft.research.frontend.integration.adapters.layout

import elan.tweaks.common.gui.geometry.HexVector
import elan.tweaks.common.gui.geometry.Rectangle
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectPalletPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectsTreePort
import thaumcraft.common.lib.research.ResearchManager
import thaumcraft.common.lib.research.ResearchNoteData
import thaumcraft.common.lib.utils.HexUtils
import java.util.*
import kotlin.math.roundToInt

class HexLayoutResearchNoteDataAdapter(
    private val bounds: Rectangle, // TODO: generalize bounds to origin and contains operator and use circular bounds here
    private val hexSize: Int,
    private val centerUiOrigin: VectorXY,
    private val aspectTree: AspectsTreePort,
    private val pallet: AspectPalletPort,
    private val notesDataProvider: () -> ResearchNoteData
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
        HexVector.roundedFrom(this - centerUiOrigin, hexSize).key

    override fun asOriginList(): List<Pair<VectorXY, AspectHex>> =
        keyToAspectHex.values
            .map { aspectHex -> aspectHex.uiCenterOrigin to aspectHex }

    // TODO extract this to separate component, which would probably also handle note data provision
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
        hexes: HashMap<String, HexUtils.Hex>,
        keyToNeighbourKeys: MutableMap<String, Set<String>>,
        traversedKeys: MutableSet<String>
    ): AspectHex {
        val uiCenter = hexes.getUiCenterBy(key)
        val uiOrigin = uiCenter - hexSize + 1 // TODO: move to hex texture object? or is it an issue of rounding when getting origin?
        val connections =
            keyToNeighbourKeys
                .getOrDefault(key, emptySet())
                .map { neighbourKey -> hexes.getUiCenterBy(neighbourKey) }
                .toSet()

        return when (type) {
            HexType.ROOT -> AspectHex.Occupied.Root(
                uiOrigin = uiOrigin,
                uiCenterOrigin = uiCenter,
                aspect = aspect,
                connectionTargetsCenters = connections
            )
            HexType.NODE -> AspectHex.Occupied.Node(
                key = key,
                uiOrigin = uiOrigin,
                uiCenterOrigin = uiCenter,
                aspect = aspect,
                onRootPath = key in traversedKeys,
                connectionTargetsCenters = connections
            )
            else -> AspectHex.Vacant(
                key = key,
                uiCenterOrigin = uiCenter,
            )
        }
    }

    private fun HashMap<String, HexUtils.Hex>.getUiCenterBy(key: String) =
        getValue(key).origin + centerUiOrigin


    private fun traversRootPathsAndBuildConnectionMap(
        hexEntries: HashMap<String, ResearchManager.HexEntry>,
        hexes: HashMap<String, HexUtils.Hex>
    ): Pair<MutableSet<String>, MutableMap<String, Set<String>>> {
        val rootHexes = hexEntries.filterValues { entry -> entry.type == HexType.ROOT }.keys

        val traversedKeys = mutableSetOf<String>()
        val relatedNodeKeys = mutableMapOf<String, Set<String>>()
        val keysToTraverse = Stack<String>()

        keysToTraverse += rootHexes

        val discoveredAspects = pallet.discoveredAspects().toSet()

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

    private fun getHexEntries() = notesDataProvider().hexEntries
    private fun getHexes() = notesDataProvider().hexes

    private fun HexUtils.Hex.neighborKey(index: Int) = getNeighbour(index).toString()

    private val HexUtils.Hex.origin
        get() = toPixel(hexSize)
            .run { Vector2D(x.roundToInt(), y.roundToInt()) } // TODO: This will probably backfire, if so - should consider using floats/doubles in  vectors

    private object HexType {
        const val VACANT = 0
        const val ROOT = 1
        const val NODE = 2
    }
}
