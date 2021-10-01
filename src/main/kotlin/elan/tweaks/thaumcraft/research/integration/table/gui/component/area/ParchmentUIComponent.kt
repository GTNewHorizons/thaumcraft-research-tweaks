package elan.tweaks.thaumcraft.research.integration.table.gui.component.area

import elan.tweaks.common.ext.drawQuads
import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.TickingUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.geometry.HexVector
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.Vector3D
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.domain.ports.provided.ResearchPort
import elan.tweaks.thaumcraft.research.integration.adapters.layout.AspectHex
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ParchmentTexture
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11
import thaumcraft.client.lib.UtilsFX

class ParchmentUIComponent(
    private val research: ResearchPort,
    private val hexLayout: HexLayout<AspectHex>,
    private val uiOrigin: VectorXY,
    private val runeLimit: Int,
    private val hexSize: Int,
    private val centerOffset: VectorXY,
) : BackgroundUIComponent, TickingUIComponent {

    private val keysToRunes: MutableMap<String, Rune> = mutableMapOf()

    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (research.missingNotes()) return

        drawParchment(context)
        drawRunes(context)
    }

    private fun drawParchment(context: UIContext) {
        ParchmentTexture.draw(
            origin = context.toScreenOrigin(uiOrigin)
        )
    }

    private fun drawRunes(context: UIContext) {
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        keysToRunes.values.forEach { rune ->
            val screenOrigin = context.toScreenOrigin(rune.uiOrigin)
            var alpha = 0.5f
            if (rune.decayProgress < 0.25f) {
                alpha = rune.decayProgress * 2.0f
            } else if (rune.decayProgress > 0.5f) {
                alpha = 1.0f - rune.decayProgress
            }

            drawRune(screenOrigin, rune.index, alpha * 0.66f)
        }
        GL11.glPopMatrix()
    }

    override fun onTick(partialTicks: Float, context: UIContext) {
        if (research.missingNotes()) {
            keysToRunes.clear()
        }
        introduceRunes(context)
        decayRunes(partialTicks)
    }

    private fun introduceRunes(context: UIContext) {
        if (research.missingNotes() || keysToRunes.size > runeLimit) return

        val vector = Vector2D(
            x = (20..130).random(),
            y = (15..130).random()
        ) - centerOffset
        val hex = HexVector.roundedFrom(vector, hexSize)

        val runeUiOrigin = hex.toVector(hexSize) + uiOrigin + centerOffset

        if (hexLayout.contains(runeUiOrigin) || keysToRunes.containsKey(hex.key)) return

        val rune = Rune(
            index = (0..16).random(), // TODO: move range to texture object?
            uiOrigin = runeUiOrigin,
            totalLifetime = (200 + context.nextRandomInt(600)).toFloat()
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

    private fun drawRune(screenOrigin: Vector3D, index: Int, alpha: Float) {
        GL11.glPushMatrix()
        UtilsFX.bindTexture("textures/misc/script.png")
        GL11.glColor4f(0.0f, 0.0f, 0.0f, alpha)
        GL11.glTranslated(screenOrigin.x.toDouble(), screenOrigin.y.toDouble(), 0.0)
        if (index < 16) {
            GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f)
        }
        val var8 = 0.0625f * index.toFloat()
        val var9 = var8 + 0.0625f
        val var10 = 0.0f
        val var11 = 1.0f
        Tessellator.instance.drawQuads {
            setColorRGBA_F(0.0f, 0.0f, 0.0f, alpha)
            addVertexWithUV(-5.0, 5.0, screenOrigin.z, var9.toDouble(), var11.toDouble())
            addVertexWithUV(5.0, 5.0, screenOrigin.z, var9.toDouble(), var10.toDouble())
            addVertexWithUV(5.0, -5.0, screenOrigin.z, var8.toDouble(), var10.toDouble())
            addVertexWithUV(-5.0, -5.0, screenOrigin.z, var8.toDouble(), var11.toDouble())
        }
        GL11.glPopMatrix()
    }

    data class Rune(
        val index: Int,
        val uiOrigin: VectorXY,
        var totalLifetime: Float
    ) {
        val decayProgress get() = timeToDecayTicks / totalLifetime
        val decayed get() = timeToDecayTicks <= 0
        var timeToDecayTicks: Float = totalLifetime
    }
}
