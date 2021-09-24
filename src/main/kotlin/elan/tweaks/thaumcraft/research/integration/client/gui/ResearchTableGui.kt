package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.common.gui.Grid
import elan.tweaks.common.gui.GridDynamicListAdapter
import elan.tweaks.common.gui.Vector
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture.AspectPools
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import org.lwjgl.opengl.GL11
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.lib.UtilsFX
import thaumcraft.common.Thaumcraft
import thaumcraft.common.lib.network.PacketHandler
import thaumcraft.common.lib.network.playerdata.PacketAspectCombinationToServer
import thaumcraft.common.tiles.TileResearchTable

class ResearchTableGui(
    container: Container,
    private val player: EntityPlayer,
    private val tileEntity: TileResearchTable,
) : GuiContainer(container) {
    init {
        xSize = ResearchTableInventoryTexture.width
        ySize = ResearchTableInventoryTexture.inventoryOrigin.y + PlayerInventoryTexture.height
    }

    private val uiOrigin get() = Vector(x = guiLeft, y = guiTop)

    private val discoveredAspects
        get() =
            Thaumcraft.proxy.getPlayerKnowledge().getAspectsDiscovered(player.commandSenderName)

    private val leftAspectPoolGrid: Grid<Aspect> = GridDynamicListAdapter(
        bounds = AspectPools.leftRectangle,
        cellSize = AspectPools.ASPECT_CELL_SIZE_PIXEL
    ) {
        discoveredAspects.aspectsSorted.filterIndexed { index, _ -> index % 2 == 0 }
    }

    private val rightAspectPoolGrid: Grid<Aspect> = GridDynamicListAdapter(
        bounds = AspectPools.rightRectangle,
        cellSize = AspectPools.ASPECT_CELL_SIZE_PIXEL
    ) {
        discoveredAspects.aspectsSorted.filterIndexed { index, _ -> index % 2 != 0 }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        drawInventory()
        drawTable()
    }

    private fun drawInventory() {
        PlayerInventoryTexture.draw(
            origin = uiOrigin + ResearchTableInventoryTexture.inventoryOrigin,
            zLevel = this.zLevel
        )
    }

    private fun drawTable() {
        ResearchTableInventoryTexture.draw(
            origin = uiOrigin,
            zLevel = this.zLevel
        )

        drawAspectPools()
    }

    private fun drawAspectPools() {

        drawAspectPool(leftAspectPoolGrid)
        drawAspectPool(rightAspectPoolGrid)
    }

    private fun drawAspectPool(aspectGrid: Grid<Aspect>) {
        val aspectTagBlend = 771
        aspectGrid.asOriginSequence().forEach { (relativeOrigin, aspect) ->
            val amount = discoveredAspects.getAmount(aspect).toFloat()
            val bonusAmount = tileEntity.bonusAspects.getAmount(aspect)

            val faded = amount <= 0 && bonusAmount <= 0
            val alpha = if (faded) 0.33f else 1.0f

            UtilsFX.drawTag(
                uiOrigin.x + relativeOrigin.x,
                uiOrigin.y + relativeOrigin.y,
                aspect,
                amount,
                bonusAmount,
                zLevel.toDouble(),
                aspectTagBlend,
                alpha
            )
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        val mousePosition = Vector(mouseX, mouseY)
        val aspect = findAspectAt(mousePosition)
        
        if (aspect != null) {
            drawTooltip(aspect, mouseX, mouseY)
        }
    }

    // TODO: Move to texture rendering object
    private fun drawTooltip(aspect: Aspect, mouseX: Int, mouseY: Int) {
        UtilsFX.drawCustomTooltip(
            this, itemRender, fontRendererObj, listOf(aspect.name, aspect.localizedDescription), mouseX, mouseY - 8, 11
        )
        // TODO: Add research check
        if (!aspect.isPrimal) {
            GL11.glPushMatrix()
            GL11.glEnable(3042)
            GL11.glBlendFunc(770, 771)
            UtilsFX.bindTexture("textures/aspects/_back.png")
            GL11.glPushMatrix()
            GL11.glTranslated((mouseX + 6).toDouble(), (mouseY + 6).toDouble(), 0.0)
            GL11.glScaled(1.25, 1.25, 0.0)
            UtilsFX.drawTexturedQuadFull(0, 0, 0.0)
            GL11.glPopMatrix()
            GL11.glPushMatrix()
            GL11.glTranslated((mouseX + 24).toDouble(), (mouseY + 6).toDouble(), 0.0)
            GL11.glScaled(1.25, 1.25, 0.0)
            UtilsFX.drawTexturedQuadFull(0, 0, 0.0)
            GL11.glPopMatrix()
            UtilsFX.drawTag(mouseX + 26, mouseY + 8, aspect.components[1], 0.0f, 0, 0.0)
            UtilsFX.drawTag(mouseX + 8, mouseY + 8, aspect.components[0], 0.0f, 0, 0.0)
            GL11.glDisable(3042)
            GL11.glPopMatrix()
        }
    }


    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        // TODO: Add RESEARCHER_2 check
        if (isShiftKeyDown()) {
            val mousePoint = Vector(mouseX, mouseY)
            // TODO: Blow logic should actually be in domain
            val aspect: Aspect? = findAspectAt(mousePoint)
            if (aspect != null && aspect.isCompound && aspect.bothComponentsPresent) {
                playButtonCombine()
                sendCombinationRequestToServer(aspect)
            }
        }
    }

    private fun findAspectAt(point: Vector): Aspect? {
        val uiPoint = point - uiOrigin
        return leftAspectPoolGrid[uiPoint] ?: rightAspectPoolGrid[uiPoint]
    }

    private val Aspect.bothComponentsPresent get() = componentPresent(0) && componentPresent(1)

    private fun Aspect.componentPresent(index: Int, minimumAmount: Int = 1) = isCompound
            && (discoveredAspects.getAmount(components[index]) + tileEntity.bonusAspects.getAmount(components[index]) >= minimumAmount)

    private val Aspect.isCompound get() = !isPrimal

    private fun playButtonCombine() {
        mc.renderViewEntity.worldObj.playSound(
            mc.renderViewEntity.posX,
            mc.renderViewEntity.posY,
            mc.renderViewEntity.posZ,
            "thaumcraft:hhon",
            0.3f,
            1.0f,
            false
        )
    }

    private fun sendCombinationRequestToServer(aspect: Aspect) {
        PacketHandler.INSTANCE.sendToServer(
            PacketAspectCombinationToServer(
                player,
                tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, aspect.components[0],
                aspect.components[1],
                tileEntity.bonusAspects.getAmount(aspect.components[0]) > 0,
                tileEntity.bonusAspects.getAmount(aspect.components[1]) > 0,
                true
            )
        )
    }

}
