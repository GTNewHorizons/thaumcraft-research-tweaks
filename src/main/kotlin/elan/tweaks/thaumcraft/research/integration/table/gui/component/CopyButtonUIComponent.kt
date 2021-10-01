package elan.tweaks.thaumcraft.research.integration.table.gui.component

import elan.tweaks.common.gui.component.*
import elan.tweaks.common.gui.drawing.AspectDrawer
import elan.tweaks.common.gui.geometry.Rectangle
import elan.tweaks.common.gui.geometry.Vector2D
import elan.tweaks.common.gui.geometry.Vector3D
import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.common.gui.peripheral.MouseButton
import elan.tweaks.thaumcraft.research.domain.ports.provided.AspectPalletPort
import elan.tweaks.thaumcraft.research.domain.ports.provided.ResearchPort
import elan.tweaks.thaumcraft.research.domain.ports.required.KnowledgeBase
import elan.tweaks.thaumcraft.research.integration.table.container.ResearchTableContainerFactory.ENCHANT_ACTION_ID
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.CopyButtonActiveTexture
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.CopyButtonNotActiveTexture
import elan.tweaks.thaumcraft.research.integration.table.gui.textures.CopyRequirementsTexture
import net.minecraft.util.StatCollector
import org.lwjgl.opengl.GL11
import thaumcraft.api.aspects.Aspect

class CopyButtonUIComponent(
    private val bounds: Rectangle,
    private val requirementsUiOrigin: VectorXY,
    private val research: ResearchPort,
    private val aspectPallet: AspectPalletPort,
    private val knowledge: KnowledgeBase,
) : BackgroundUIComponent, MouseOverUIComponent, ClickableUIComponent, TickingUIComponent {

    private val clickDelayTicks = 10f
    private var clickedCoolDown = 0f
    private val onCoolDown get() = clickedCoolDown > 0
    
    override fun onDrawBackground(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (knowledge.notDiscoveredResearchDuplication()) return

        val screenOrigin = context.toScreenOrigin(bounds.origin)

        if (
            research.notReadyForDuplication() 
            // TODO: would be nice to also check for ink and paper in player's inventory
            || aspectPallet.missing(research.usedAspectAmounts)
            || onCoolDown
        ) drawNotActiveTexture(screenOrigin)
        else drawActiveTexture(screenOrigin)

    }

    private fun drawNotActiveTexture(screenOrigin: Vector3D) {
        CopyButtonNotActiveTexture.draw(screenOrigin)
    }
    
    private fun drawActiveTexture(screenOrigin: Vector3D) {
        CopyButtonActiveTexture.draw(screenOrigin)
    }

    override fun onMouseClicked(uiMousePosition: VectorXY, button: MouseButton, context: UIContext) {
        if (uiMousePosition !in bounds || knowledge.notDiscoveredResearchDuplication() || research.notReadyForDuplication() || onCoolDown) return
        
        clickedCoolDown = clickDelayTicks
        context.sendEnchantPacket(actionId = ENCHANT_ACTION_ID)
        context.playWrite()
    }

    private fun UIContext.playWrite() = apply {
        playSoundOnEntity(
            soundName = "thaumcraft:cameraclack",
            volume = 0.4f,
            pitch = 1.0f,
            distanceDelay = false
        )
    }

    override fun onTick(partialTicks: Float, context: UIContext) {
        if (clickedCoolDown <= 0) return
        clickedCoolDown -= partialTicks
    }

    override fun onMouseOver(uiMousePosition: VectorXY, partialTicks: Float, context: UIContext) {
        if (uiMousePosition !in bounds || knowledge.notDiscoveredResearchDuplication() || research.notReadyForDuplication()) return

        drawRequirements(research.usedAspectAmounts, context)
    }

    private fun drawRequirements(usedAspectAmounts: Map<Aspect, Int>, context: UIContext) {
        val screenOrigin = context.toScreenOrigin(requirementsUiOrigin)

        val header = StatCollector.translateToLocal("tc.research.copy")
        

        GL11.glPushMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        context.fontRenderer.drawStringWithShadow(header, screenOrigin.x, screenOrigin.y, -1)
        val textHeight = 10
        CopyRequirementsTexture.draw(screenOrigin + Vector2D(x = 0, y  = textHeight))
        GL11.glPopMatrix()

        usedAspectAmounts
            // TODO: add order
            .entries
            .forEachIndexed { index, (aspect, amount) ->
                val tagOffset = Vector2D(
                    x = CopyRequirementsTexture.width + index * 16,
                    y = textHeight
                )
                AspectDrawer.drawTag(screenOrigin + tagOffset, aspect, amount.toFloat())
            }
        
    }

}
