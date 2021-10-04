package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component.area

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.TickingUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.HexVector
import elan.tweaks.common.gui.dto.Rgba
import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.integration.adapters.layout.AspectHex
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.HexTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ParchmentTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.RuneTexture

class ParchmentUIComponent(
    private val research: ResearchProcessPort,
    private val hexLayout: HexLayout<AspectHex>,
    private val uiOrigin: VectorXY,
    private val runeLimit: Int,
    private val centerOffset: VectorXY,
) : BackgroundUIComponent, TickingUIComponent {

    private val keysToRunes: MutableMap<String, Rune> = mutableMapOf()

    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (research.missingNotes()) return

        drawParchment(context)
        drawRunes(context)
    }

    private fun drawParchment(context: UIContext) {
        context.drawBlending(ParchmentTexture, uiOrigin)
    }

    private fun drawRunes(context: UIContext) {
        keysToRunes.values.forEach { rune ->
            val colorMask = Rgba(0f, 0f, 0f, rune.alpha )
            context.drawBlending(rune.texture, rune.uiOrigin, colorMask)
        }
    }

    override fun onTick(partialTicks: Float, context: UIContext) {
        if (research.missingNotes()) {
            keysToRunes.clear()
        }
        introduceRunes(context)
        decayRunes(partialTicks)
    }

    private fun introduceRunes(context: UIContext) {
        if (research.missingNotes() || keysToRunes.size >= runeLimit) return

        val vector = Vector2D(
            x = (20..130).random(),
            y = (15..130).random()
        ) - centerOffset
        val hex = HexVector.roundedFrom(vector, HexTexture.SIZE_PIXELS)

        val runeUiOrigin = hex.toVector(HexTexture.SIZE_PIXELS) + uiOrigin + centerOffset - HexTexture.SIZE_PIXELS + 1

        val aspectHex = hexLayout[runeUiOrigin]
        if (
            (research.incomplete() && aspectHex != null) 
            || (!research.incomplete() && aspectHex is AspectHex.Occupied) 
            || keysToRunes.containsKey(hex.key)) return

        val rune = Rune(
            uiOrigin = runeUiOrigin,
            totalLifetime = 200f + context.random.nextInt(700)
        )
        keysToRunes += hex.key to rune
    }

    private fun decayRunes(partialTicks: Float) {
        val decayedRunes = keysToRunes
            .onEach { (_, rune) ->
                rune.timeToDecayTicks -= partialTicks
            }.filterValues(Rune::decayed)

        keysToRunes -= decayedRunes.keys
    }

    // TODO: wrap lifecycle in container
    data class Rune(
        val uiOrigin: VectorXY,
        var totalLifetime: Float
    ) {
        val texture: RuneTexture = RuneTexture.random()

        val alpha
            get() = 0.66f * when {
                decayProgress < 0.25f -> decayProgress * 2.0f
                decayProgress > 0.5f -> 1.0f - decayProgress
                else -> 0.5f
            }

        private val decayProgress get() = timeToDecayTicks / totalLifetime
        
        val decayed get() = timeToDecayTicks <= 0
        var timeToDecayTicks: Float = totalLifetime
    }
}
