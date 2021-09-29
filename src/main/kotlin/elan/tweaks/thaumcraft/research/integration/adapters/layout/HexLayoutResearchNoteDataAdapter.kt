package elan.tweaks.thaumcraft.research.integration.adapters.layout

import elan.tweaks.common.gui.geometry.Rectangle
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import thaumcraft.common.lib.research.ResearchNoteData
import thaumcraft.common.lib.utils.HexUtils
import kotlin.math.roundToInt
import kotlin.math.sqrt

class HexLayoutResearchNoteDataAdapter(
    private val bounds: Rectangle, // TODO: generalize bounds to origin and contains operator and use circular bounds here
    private val hexSize: Int,
    private val centerUiOrigin: VectorXY,
    private val notesDataProvider: () -> ResearchNoteData
) : HexLayout<AspectHex> {
    private val hexEntries get() = notesDataProvider().hexEntries
    private val hexes get() = notesDataProvider().hexes

    override fun contains(uiPoint: VectorXY): Boolean {
        if (uiPoint !in bounds) return false
        
        val hexKey = (uiPoint - centerUiOrigin).toHexKey()
        return hexEntries.containsKey(hexKey) && hexes.containsKey(hexKey)
    }

    override fun get(uiPoint: VectorXY): AspectHex? {
        if (uiPoint !in bounds) return null
        
        val hexKey = uiPoint.toHexKey()
        return toHex(hexKey)
    }

    override fun asOriginSequence(): Sequence<Pair<VectorXY, AspectHex>> =
        hexes
            .keys
            .asSequence()
            .mapNotNull(this::toHex)
            .map { it.uiOrigin to it }

    private fun toHex(hexKey: String): AspectHex? {
        val entry = hexEntries[hexKey] ?: return null
        val hexCentralOrigin = hexes[hexKey]?.origin ?: return null
        val uiCenter = hexCentralOrigin + centerUiOrigin
        val uiOrigin = uiCenter - hexSize + 1 // move to hex texture

        return when (entry.type) {
            HexType.VACANT -> AspectHex.Vacant(uiCenter)
            // TODO: evaluate connections here
            HexType.ROOT -> AspectHex.Occupied.Root(uiOrigin, uiCenter, entry.aspect, setOf(uiCenter + Vector2D(9,9)))
            // TODO: evaluate connections here
            HexType.NODE -> AspectHex.Occupied.Node(uiOrigin, uiCenter, entry.aspect, disconnectedFromRoot = false, emptySet()) 
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
