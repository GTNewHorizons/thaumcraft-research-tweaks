package elan.tweaks.thaumcraft.research.integration.adapters

import elan.tweaks.common.gui.geometry.Rectangle
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.domain.model.Research
import thaumcraft.common.lib.research.ResearchNoteData
import thaumcraft.common.lib.utils.HexUtils
import kotlin.math.roundToInt
import kotlin.math.sqrt

class HexLayoutResearchNoteDataAdapter(
    private val bounds: Rectangle, // TODO: generalize bounds to origin and contains operator and use circular bounds here
    private val hexSize: Int,
    private val centerUiOrigin: VectorXY,
    private val notesDataProvider: () -> ResearchNoteData
) : HexLayout<Research.Hex> {
    private val hexEntries get() = notesDataProvider().hexEntries
    private val hexes get() = notesDataProvider().hexes

    override fun contains(uiPoint: VectorXY): Boolean {
        if (uiPoint !in bounds) return false
        
        val hexKey = (uiPoint - centerUiOrigin).toHexKey()
        return hexEntries.containsKey(hexKey) && hexes.containsKey(hexKey)
    }

    override fun get(uiPoint: VectorXY): Research.Hex? {
        if (uiPoint !in bounds) return null
        
        val hexKey = uiPoint.toHexKey()
        return toHex(hexKey)
    }

    override fun asOriginSequence(): Sequence<Pair<VectorXY, Research.Hex>> =
        hexes
            .keys
            .asSequence()
            .mapNotNull(this::toHex)
            .map { it.uiOrigin to it }

    private fun toHex(hexKey: String): Research.Hex? {
        val entry = hexEntries[hexKey] ?: return null
        val hexCentralOrigin = hexes[hexKey]?.origin ?: return null
        val uiOrigin = hexCentralOrigin + centerUiOrigin

        return when (entry.type) {
            HexType.VACANT -> Research.Hex.Vacant(uiOrigin)
            HexType.ROOT -> Research.Hex.Occupied.Root(uiOrigin, entry.aspect)
            HexType.NODE -> Research.Hex.Occupied.Node(uiOrigin, entry.aspect)
            else -> null // TODO: should not get here afaik, log if does
        }
    }

    private fun VectorXY.toHexKey(): String {
        val q: Double = 0.6666666666666666 * this.x / hexSize.toDouble()
        val r: Double = (0.3333333333333333 * sqrt(3.0) * -this.y - 0.3333333333333333 * this.x) / hexSize.toDouble()
        return HexUtils.getRoundedHex(q, r).toString()
    }

    private val HexUtils.Hex.origin
        get() = toPixel(hexSize)
            .run { Vector2D(x.roundToInt(), y.roundToInt()) } // TODO: This will probably backfire, if so - should consider using floats/doubles in  vectors

    private object HexType {
        const val VACANT = 0
        const val ROOT = 1
        const val NODE = 2
    }
}
