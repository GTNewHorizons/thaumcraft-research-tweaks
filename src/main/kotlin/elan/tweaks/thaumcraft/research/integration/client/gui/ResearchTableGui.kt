package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture.AspectPools
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import org.lwjgl.opengl.GL11
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.aspects.AspectList
import thaumcraft.client.lib.UtilsFX
import thaumcraft.common.Thaumcraft
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

    private val origin get() = Vector(x = guiLeft, y = guiTop)

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        drawInventory()
        drawTable()
    }

    private fun drawInventory() {
        PlayerInventoryTexture.draw(
            origin = origin + ResearchTableInventoryTexture.inventoryOrigin,
            zLevel = this.zLevel
        )
    }

    private fun drawTable() {
        ResearchTableInventoryTexture.draw(
            origin = origin,
            zLevel = this.zLevel
        )

        drawAspectPools()
    }

    private fun drawAspectPools() {
        val aspects = Thaumcraft.proxy.getPlayerKnowledge().getAspectsDiscovered(player.commandSenderName)
        val bonusAspects = this.tileEntity.bonusAspects

        val (left, right) = partitionAspectsToLeftAndRightPool()

        drawAspectPool(AspectPools.leftOrigin, left, aspects, bonusAspects)
        drawAspectPool(AspectPools.rightOrigin, right, aspects, bonusAspects)
    }

    private fun drawAspectPool(
        poolOrigin: Vector,
        aspects: List<Aspect>,
        allAspects: AspectList,
        bonusAspects: AspectList
    ) {
        val aspectTagBlend = 771
        val absoluteOrigin = origin + poolOrigin
        aspects.forEachIndexed { index, aspect ->
            val faded = allAspects.getAmount(aspect) <= 0 && bonusAspects.getAmount(aspect) <= 0
            val alpha = if (faded) 0.33f else 1.0f
            val xOffset = (index % AspectPools.COLUMNS) * AspectPools.ASPECT_SIZE_PIXEL
            val yOffset = (index / AspectPools.COLUMNS) * AspectPools.ASPECT_SIZE_PIXEL
            UtilsFX.drawTag(
                absoluteOrigin.x + xOffset,
                absoluteOrigin.y + yOffset,
                aspect,
                allAspects.getAmount(aspect).toFloat(),
                bonusAspects.getAmount(aspect),
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

    private fun findAspectAt(point: Vector): Aspect? {
        val uiPoint = point - origin
        val (left, right) = partitionAspectsToLeftAndRightPool()

        return when (uiPoint) {
            in AspectPools.leftRectangle -> findAspectAt(uiPoint, AspectPools.leftRectangle, left)
            in AspectPools.rightRectangle -> findAspectAt(uiPoint, AspectPools.rightRectangle, right)
            else -> null
        }
    }

    private fun partitionAspectsToLeftAndRightPool(): Pair<List<Aspect>, List<Aspect>> {
        val aspects = Thaumcraft.proxy.getPlayerKnowledge().getAspectsDiscovered(player.commandSenderName)

        val left = aspects.aspectsSorted.filterIndexed { index, _ -> index % 2 == 0 }
        val right = aspects.aspectsSorted.filterIndexed { index, _ -> index % 2 != 0 }

        return Pair(left, right)
    }
    
    private fun findAspectAt(uiPoint: Vector, poolRectangle: Rectangle, aspects: List<Aspect>): Aspect? =
        when (val aspectIndex = deduceAspectIndex(uiPoint, poolRectangle)) {
            in aspects.indices -> aspects[aspectIndex]
            else -> null
        }

    private fun deduceAspectIndex(
        uiPoint: Vector,
        poolRectangle: Rectangle
    ): Int {
        val poolPoint = uiPoint - poolRectangle.origin
        val columnIndex = (poolPoint.x / AspectPools.ASPECT_SIZE_PIXEL).coerceAtMost(AspectPools.COLUMNS - 1)
        val rowIndex = (poolPoint.y / AspectPools.ASPECT_SIZE_PIXEL).coerceAtMost(AspectPools.ROWS - 1)
        return columnIndex + rowIndex * AspectPools.COLUMNS
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
    }

}
