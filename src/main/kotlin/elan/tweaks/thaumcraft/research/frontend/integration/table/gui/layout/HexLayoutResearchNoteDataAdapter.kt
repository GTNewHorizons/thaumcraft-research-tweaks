package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout

import elan.tweaks.common.ext.HexMath
import elan.tweaks.common.ext.HexMath.toCenterVector
import elan.tweaks.common.ext.allNeighbors
import elan.tweaks.common.ext.key
import elan.tweaks.common.gui.dto.HexVector
import elan.tweaks.common.gui.dto.Rectangle
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectsTreePort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort
import elan.tweaks.thaumcraft.research.frontend.integration.adapters.ResearchNotesAdapter
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.dto.AspectHex
import java.util.*
import thaumcraft.common.lib.research.ResearchManager
import thaumcraft.common.lib.utils.HexUtils

class HexLayoutResearchNoteDataAdapter(
    private val bounds: Rectangle,
    private val centerUiOrigin: VectorXY,
    private val hexSize: Int,
    private val aspectTree: AspectsTreePort,
    private val researcher: ResearcherKnowledgePort,
    private val researchProcess: ResearchProcessPort
) : HexLayout<AspectHex> {

  private var cache: Cache = generateCache()

  override fun contains(uiPoint: VectorXY): Boolean {
    if (uiPoint !in bounds) return false

    val hexKey = uiPoint.toHexKey()
    return hexPresent(hexKey)
  }

  override fun get(uiPoint: VectorXY): AspectHex? {
    if (uiPoint !in bounds) return null

    val hexKey = uiPoint.toHexKey()
    return getValidKeyToAspectHexMap()[hexKey]
  }

  private fun VectorXY.toHexKey(): String =
      HexVector.roundedFrom(this - centerUiOrigin, hexSize).key

  override fun asOriginList(): List<Pair<VectorXY, AspectHex>> =
      getValidKeyToAspectHexMap().values.map { aspectHex -> aspectHex.uiOrigin to aspectHex }

  private fun getValidKeyToAspectHexMap(): Map<String, AspectHex> {
    if (cache.hasExpired()) {
      cache = generateCache()
    }
    return cache.keyToAspectHex
  }

  private fun generateCache(): Cache {
    if (researchProcess.notesCorrupted()) return Cache(emptyMap(), emptyMap(), emptyMap())

    val hexEntries = getHexEntries()
    val hexes = getHexes()

    val (traversedKeys, keyToNeighbourKeys) =
        traversRootPathsAndBuildConnectionMap(hexEntries, hexes)

    val keyToAspectHex =
        hexEntries
            .mapValues { (key, entry) ->
              entry.convertToAspectHex(key, hexes, keyToNeighbourKeys, traversedKeys)
            }
            .toMap()

    return Cache(keyToAspectHex, hexes, hexEntries)
  }

  // A better implementation would change the core data structure to contain current map connection
  // state and would update it on any change
  private fun traversRootPathsAndBuildConnectionMap(
      hexEntries: Map<String, ResearchManager.HexEntry>,
      hexes: Map<String, HexUtils.Hex>
  ): Pair<Set<String>, Map<String, Set<String>>> {
    val rootHexes =
        hexEntries.filterValues { entry -> entry.type == ResearchNotesAdapter.HexType.ROOT }.keys

    val traversedKeys = mutableSetOf<String>()
    val relatedNodeKeys = mutableMapOf<String, Set<String>>()
    val keysToTraverse = Stack<String>()

    keysToTraverse += rootHexes

    val discoveredAspects = researcher.allDiscoveredAspects().toSet()

    while (keysToTraverse.isNotEmpty()) {
      val key = keysToTraverse.pop()
      val entry = hexEntries[key] ?: continue
      val hex: HexUtils.Hex = hexes[key] ?: continue
      if (key in traversedKeys || entry.aspect !in discoveredAspects) continue

      val newRelatedNeighbors =
          hex.allNeighbors
              .map { neighbor -> neighbor.key }
              .filter(this::hexPresent)
              .filter { neighbourKey ->
                val neighborEntry = hexEntries[neighbourKey]!!
                neighbourKey !in traversedKeys &&
                    neighborEntry.type != ResearchNotesAdapter.HexType.VACANT &&
                    neighborEntry.aspect in discoveredAspects &&
                    aspectTree.areRelated(entry.aspect, neighborEntry.aspect)
              }
              .toSet()

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

  private fun ResearchManager.HexEntry.convertToAspectHex(
      key: String,
      hexes: Map<String, HexUtils.Hex>,
      keyToNeighbourKeys: Map<String, Set<String>>,
      traversedKeys: Set<String>
  ): AspectHex {
    val uiCenter = hexes.findUICenterOf(key)
    val uiOrigin = HexMath.toOrigin(uiCenter, hexSize)
    val connections =
        keyToNeighbourKeys
            .getOrDefault(key, emptySet())
            .map { neighbourKey -> hexes.findUICenterOf(neighbourKey) }
            .toSet()

    return when (type) {
      ResearchNotesAdapter.HexType.ROOT ->
          AspectHex.Occupied.Root(
              uiOrigin = uiOrigin,
              uiCenter = uiCenter,
              aspect = aspect,
              connectionTargetsCenters = connections)
      ResearchNotesAdapter.HexType.NODE ->
          AspectHex.Occupied.Node(
              key = key,
              uiOrigin = uiOrigin,
              uiCenter = uiCenter,
              aspect = aspect,
              onRootPath = key in traversedKeys,
              connectionTargetsCenters = connections)
      else ->
          AspectHex.Vacant(
              key = key,
              uiOrigin = uiOrigin,
          )
    }
  }

  private fun Map<String, HexUtils.Hex>.findUICenterOf(key: String) =
      toCenterVector(hex = getValue(key), hexSize) + centerUiOrigin

  private inner class Cache(
      val keyToAspectHex: Map<String, AspectHex>,
      val hexes: Map<String, HexUtils.Hex>,
      val hexEntries: Map<String, ResearchManager.HexEntry>,
  ) {
    fun hasExpired(): Boolean {
      val data = researchProcess.data
      return data == null ||
          hexes.size != data.hexes.size ||
          hexEntries.size != data.hexEntries.size ||
          hexes.any { (key, hex) -> hex.notEqual(data.hexes[key]) } ||
          hexEntries.any { (key, entry) -> entry.notEqual(data.hexEntries[key]) }
    }

    private fun HexUtils.Hex.notEqual(other: HexUtils.Hex?) = other == null || !equals(other)

    private fun ResearchManager.HexEntry.notEqual(other: ResearchManager.HexEntry?) =
        other == null || type != other.type || aspect?.tag != other.aspect?.tag
  }
}
