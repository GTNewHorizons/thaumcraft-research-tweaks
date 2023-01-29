package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component.area

import elan.tweaks.common.gui.dto.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.dto.AspectHex
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout.ParchmentHexMapLayout
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.RuneTexture
import java.util.*

class Runes(
    private val uiOrigin: VectorXY,
    private val runeLimit: Int,
    private val hexLayout: HexLayout<AspectHex>,
    private val research: ResearchProcessPort
) {
  private val keysToRunes: MutableMap<String, Rune> = mutableMapOf()

  fun forEach(action: (Rune) -> Unit) = keysToRunes.values.forEach(action)

  fun clear() = keysToRunes.clear()

  fun update(partialTicks: Float, random: Random) {
    introduceRunes(random)
    decayRunes(partialTicks)
  }

  private fun introduceRunes(random: Random) {
    if (research.missingNotes() || research.notesCorrupted()) return

    if (keysToRunes.size >= runeLimit) return

    val (key, hex) = ParchmentHexMapLayout.randomHex()
    if (keysToRunes.containsKey(key)) return

    val aspectHex = hexLayout[hex.center + uiOrigin]
    if (obstructingExisting(aspectHex)) return

    val rune =
        Rune(
            uiOrigin = hex.origin + uiOrigin + RuneTexture.offsetToFitInHexCenter,
            totalLifetime = 200f + random.nextInt(700))
    keysToRunes += key to rune
  }

  private fun obstructingExisting(aspectHex: AspectHex?) =
      (research.incomplete() && aspectHex != null) ||
          (!research.incomplete() && aspectHex is AspectHex.Occupied)

  private fun decayRunes(partialTicks: Float) {
    val decayedRunes =
        keysToRunes.onEach { (_, rune) -> rune.decay(partialTicks) }.filterValues(Rune::decayed)

    keysToRunes -= decayedRunes.keys
  }

  data class Rune(val uiOrigin: VectorXY, var totalLifetime: Float) {
    val texture: RuneTexture = RuneTexture.random()

    val alpha: Float
      get() =
          0.66f *
              when {
                decayProgress < 0.25f -> decayProgress * 2.0f
                decayProgress > 0.5f -> 1.0f - decayProgress
                else -> 0.5f
              }

    val decayed
      get() = timeToDecayTicks <= 0

    private val decayProgress
      get() = timeToDecayTicks / totalLifetime
    private var timeToDecayTicks: Float = totalLifetime

    fun decay(ticks: Float) {
      timeToDecayTicks -= ticks
    }
  }
}
