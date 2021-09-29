package elan.tweaks.thaumcraft.research.integration.table.gui.component

import elan.tweaks.common.gui.component.BackgroundUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.layout.hex.HexLayout
import elan.tweaks.thaumcraft.research.domain.model.Research
import elan.tweaks.thaumcraft.research.domain.ports.provided.ResearchPort
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.ParchmentTexture
import org.lwjgl.opengl.GL11
import thaumcraft.client.lib.UtilsFX

class ResearchAreaUIComponent(
    private val area: ResearchPort,
    private val hexLayout:  HexLayout<Research.Hex>,
    private val uiOrigin: VectorXY
) : BackgroundUIComponent {
    
    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if(area.missingOrComplete()) return

        drawBackgroundParchment(context)
        drawHexes(context)
    }

    private fun drawBackgroundParchment(context: UIContext) {
        ParchmentTexture.draw(
            origin = context.toScreenOrigin(uiOrigin),
            disableStandardLightning = true
        )
    }

    private fun drawHexes(context: UIContext) {
        hexLayout
            .asOriginSequence()
            .forEach { (uiOrigin, hex) -> 
                drawHexBorder(uiOrigin, context)
            }
    }

    // TODO: Move to hex texture drawing
    private fun drawHexBorder(uiOrigin: VectorXY, context: UIContext) {
        GL11.glPushMatrix()
        GL11.glAlphaFunc(516, 0.003921569f)
        GL11.glEnable(GL11.GL_BLEND)
        UtilsFX.bindTexture("textures/gui/hex1.png")
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.25f)
        
        val screenOrigin = context.toScreenOrigin(uiOrigin)
        GL11.glTranslated(screenOrigin.x.toDouble(), screenOrigin.y.toDouble(), 0.0)
        context.drawQuads {
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
