package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component.area

import elan.tweaks.common.ext.drawQuads
import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.drawing.AspectDrawer
import elan.tweaks.common.gui.fx.LineParticle
import elan.tweaks.common.gui.fx.OrbParticle
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.integration.adapters.layout.AspectHex
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.lib.UtilsFX

class AspectHexMapUIComponent(
    private val research: ResearchProcessPort,
    private val hexLayout: HexLayout<AspectHex>,
) : BackgroundUIComponent {

    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (research.missingNotes()) return

        // with current highlight texture best effect us achieved when drawing it under border, TODO: combined texture to draw over with
        drawMouseOverHighlight(uiMousePosition, context)
        drawHexes(context)
    }

    private fun drawMouseOverHighlight(uiMousePosition: VectorXY, context: UIContext) {
        if (research.notEditable()) return
        val hex = hexLayout[uiMousePosition] ?: return
        if (hex is AspectHex.Occupied.Root) return

        drawHexHighlight(hex.uiCenterOrigin, context)
    }

    // TODO: Extract to hex texture
    private fun drawHexHighlight(uiOrigin: VectorXY, context: UIContext) {
        val screenOrigin = context.toScreenOrigin(uiOrigin)
        GL11.glPushMatrix()
        GL11.glAlphaFunc(516, 0.003921569f)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(770, 1)
        UtilsFX.bindTexture("textures/gui/hex2.png")
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glTranslated(screenOrigin.x.toDouble(), screenOrigin.y.toDouble(), 0.0)
        Tessellator.instance.drawQuads {
            setBrightness(240)
            setColorRGBA_F(1.0f, 1.0f, 1.0f, 1.0f)
            addVertexWithUV(-8.0, 8.0, screenOrigin.z, 0.0, 1.0)
            addVertexWithUV(8.0, 8.0, screenOrigin.z, 1.0, 1.0)
            addVertexWithUV(8.0, -8.0, screenOrigin.z, 1.0, 0.0)
            addVertexWithUV(-8.0, -8.0, screenOrigin.z, 0.0, 0.0)
        }
        GL11.glBlendFunc(770, 771)
        GL11.glAlphaFunc(516, 0.1f)
        GL11.glPopMatrix()
    }

    private fun drawHexes(context: UIContext) {
        hexLayout
            .asOriginList()
            .onEach { (uiCenterOrigin, hex) ->
                if (hex !is AspectHex.Occupied.Root && research.incomplete()) 
                    drawHexBorder(uiCenterOrigin, context)
            }
            .map(Pair<VectorXY, AspectHex>::second)
            .filterIsInstance<AspectHex.Occupied>()
            .onEach { hex ->
                drawConnections(hex, context)
            }.forEach { hex ->
                when (hex) {
                    is AspectHex.Occupied.Root -> draw(hex, context)
                    is AspectHex.Occupied.Node -> draw(hex, context)
                }
            }
    }

    private fun draw(hex: AspectHex.Occupied.Root, context: UIContext) {
        OrbParticle.draw(context.toScreenOrigin(hex.uiCenterOrigin))
        if (research.shouldObfuscate(hex.aspect)) drawUnknownAspectTexture(hex.uiOrigin, context)
        else drawAspect(hex, context)
    }

    private fun draw(hex: AspectHex.Occupied.Node, context: UIContext) {
        when {
            research.shouldObfuscate(hex.aspect) -> drawUnknownAspectTexture(hex.uiOrigin, context)
            hex.notOnRootPath -> drawInactiveAspect(hex.uiOrigin, hex.aspect, context)
            else -> drawAspect(hex, context)
        }
    }

    // TODO: Extract to texture
    private fun drawUnknownAspectTexture(uiOrigin: VectorXY, context: UIContext) {
        val screenOrigin = context.toScreenOrigin(uiOrigin)
        UtilsFX.bindTexture("textures/aspects/_unknown.png")
        GL11.glPushMatrix()

        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(770, 771)
        GL11.glTranslated(screenOrigin.x.toDouble(), screenOrigin.y.toDouble(), 0.0)
        UtilsFX.drawTexturedQuadFull(0, 0, screenOrigin.z)
        GL11.glEnable(GL11.GL_BLEND)

        GL11.glPopMatrix()
    }

    private fun drawInactiveAspect(uiOrigin: VectorXY, aspect: Aspect, context: UIContext) {
        val screenOrigin = context.toScreenOrigin(uiOrigin)
        val alpha = 0.66f
        AspectDrawer.drawMonochromeTag(screenOrigin, aspect, alpha)
    }

    private fun drawAspect(hex: AspectHex.Occupied, context: UIContext) {
        val screenOrigin = context.toScreenOrigin(hex.uiOrigin)
        val alpha = 1.0f

        AspectDrawer.drawTag(screenOrigin, hex.aspect, alpha = alpha)
    }

    private fun drawConnections(hex: AspectHex.Occupied, context: UIContext) {
        val sourceCenter = context.toScreenOrigin(hex.uiCenterOrigin)
        hex.connectionTargetsCenters.forEach { connectionTargetCenter ->
            val targetCenter = context.toScreenOrigin(connectionTargetCenter)
            LineParticle.draw(sourceCenter, targetCenter)
        }
    }

    // TODO: Extract to hex texture
    private fun drawHexBorder(uiOrigin: VectorXY, context: UIContext) {
        val screenOrigin = context.toScreenOrigin(uiOrigin)
        
        GL11.glPushMatrix()
        GL11.glAlphaFunc(516, 0.003921569f)
        GL11.glEnable(GL11.GL_BLEND)
        UtilsFX.bindTexture("textures/gui/hex1.png")
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.25f)
        GL11.glTranslated(screenOrigin.x.toDouble(), screenOrigin.y.toDouble(), 0.0)
        Tessellator.instance.drawQuads {
            setBrightness(240)
            setColorRGBA_F(1.0f, 1.0f, 1.0f, 0.25f)
            addVertexWithUV(-8.0, 8.0, screenOrigin.z, 0.0, 1.0)
            addVertexWithUV(8.0, 8.0, screenOrigin.z, 1.0, 1.0)
            addVertexWithUV(8.0, -8.0, screenOrigin.z, 1.0, 0.0)
            addVertexWithUV(-8.0, -8.0, screenOrigin.z, 0.0, 0.0)
        }
        GL11.glAlphaFunc(516, 0.1f)
        GL11.glPopMatrix()
    }

}
