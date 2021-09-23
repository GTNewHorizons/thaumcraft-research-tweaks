package elan.tweaks.thaumcraft.research.integration.client.gui

import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.ResearchTableInventoryTexture.AspectPools
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
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

    private fun drawTable() {
        ResearchTableInventoryTexture.draw(
            origin = origin,
            zLevel = this.zLevel
        )

        val aspects = Thaumcraft.proxy.getPlayerKnowledge().getAspectsDiscovered(player.commandSenderName)
        val bonusAspects = this.tileEntity.bonusAspects

        val left = aspects.aspectsSorted.filterIndexed { index, _ -> index % 2 == 0 }
        val right = aspects.aspectsSorted.filterIndexed { index, _ -> index % 2 != 0 }

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

    private fun drawInventory() {
        PlayerInventoryTexture.draw(
            origin = origin + ResearchTableInventoryTexture.inventoryOrigin,
            zLevel = this.zLevel
        )
    }

}
