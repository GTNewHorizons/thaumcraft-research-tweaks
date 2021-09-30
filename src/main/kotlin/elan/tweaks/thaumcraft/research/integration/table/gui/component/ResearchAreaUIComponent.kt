package elan.tweaks.thaumcraft.research.integration.table.gui.component

import elan.tweaks.common.ext.drawQuads
import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.ClickableUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.component.dragndrop.DropDestinationUIComponent
import elan.tweaks.common.gui.drawing.AspectDrawer
import elan.tweaks.common.gui.fx.LineParticle
import elan.tweaks.common.gui.fx.OrbParticle
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.common.gui.peripheral.MouseButton
import elan.tweaks.thaumcraft.research.domain.ports.provided.ResearchPort
import elan.tweaks.thaumcraft.research.integration.adapters.layout.AspectHex
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ParchmentTexture
import net.minecraft.client.renderer.Tessellator
import org.lwjgl.opengl.GL11
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.lib.UtilsFX

class ResearchAreaUIComponent(
    private val research: ResearchPort,
    private val hexLayout: HexLayout<AspectHex>,
    private val uiOrigin: VectorXY
) : BackgroundUIComponent,
    ClickableUIComponent, DropDestinationUIComponent {

    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (research.missingNotes()) return

        drawBackgroundParchment(context)
        drawHexes(context)
    }

    private fun drawBackgroundParchment(context: UIContext) {
        ParchmentTexture.draw(
            origin = context.toScreenOrigin(uiOrigin)
        )
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

    // TODO: Extract to texture
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

    override fun onMouseClicked(uiMousePosition: VectorXY, button: MouseButton, context: UIContext) {
        if (research.notEditable()) return
        val hex = hexLayout[uiMousePosition] ?: return
        if(hex !is AspectHex.Occupied.Node) return // TODO: this check should be a part of domain
        
        research.erase(hex.key).onSuccess {
            context
                .playCombine()
                .playErase()
        }
    }

    override fun onDropped(draggable: Any, uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (research.notEditable()) return
        if (draggable !is Aspect) return
        val hex = hexLayout[uiMousePosition]
        if (hex !is AspectHex.Vacant) return // TODO: this check should be a part of domain

        research.write(hex.key, draggable).onSuccess {
            context
                .playCombine()
                .playWrite()
        }
    }

    private fun UIContext.playCombine() = apply {
        playSoundOnEntity(
            soundName = "thaumcraft:hhon",
            volume = 0.3f,
            pitch = 1.0f,
            distanceDelay = false
        )
    }

    private fun UIContext.playWrite() = apply {
        playSoundOnEntity(
            soundName = "thaumcraft:write",
            volume = 0.2f,
            pitch = 1.0f,
            distanceDelay = false
        )
    }

    private fun UIContext.playErase() = apply {
        playSoundOnEntity(
            soundName = "thaumcraft:erase",
            volume = 0.2f,
            pitch = 1.0f + nextRandomFloat() * 0.1f,
            distanceDelay = false
        )
    }

}
