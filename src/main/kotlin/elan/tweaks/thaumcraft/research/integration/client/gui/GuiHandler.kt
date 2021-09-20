package elan.tweaks.thaumcraft.research.integration.client.gui

import cpw.mods.fml.common.network.IGuiHandler
import elan.tweaks.thaumcraft.research.integration.client.gui.SpecializedSlot.Companion.specializedOn
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture.HOTBAR_ROW_DELIMITER_HEIGHT_PIXELS
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture.HOTBAR_ROW_INDEX
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture.SLOTS_IN_INVENTORY_ROW
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture.SLOT_SIZE_PIXELS
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture.columnIndexes
import elan.tweaks.thaumcraft.research.integration.client.gui.textures.PlayerInventoryTexture.internalRowIndexes
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import thaumcraft.api.IScribeTools
import thaumcraft.common.container.ContainerResearchTable
import thaumcraft.common.items.ItemResearchNotes
import thaumcraft.common.tiles.TileResearchTable

class GuiHandler : IGuiHandler {

    override fun getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? =
        if (id == IDs.RESEARCH_TABLE) {
            ContainerResearchTable(player.inventory, world.getTileEntity(x, y, z) as TileResearchTable)
        } else {
            null
        }


    override fun getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? =
        if (world is WorldClient && id == IDs.RESEARCH_TABLE)
            ReseearchTableGui(
                player,
                world.getTileEntity(x, y, z) as TileResearchTable,
            )
        else null

    object IDs {
        const val RESEARCH_TABLE = 0
    }
}

/**
 * Slot coordinates are set at slot creation.
 * There isn't a convenient way to change or offset their location.
 * This implementation works around that by specifically chosen ui size.
 */
class ReseearchTableGui(
    private val player: EntityPlayer,
    private val tileEntity: TileResearchTable,
) : GuiContainer(
    ResearchTableSlotContainer.create(player.inventory, tileEntity)
) {
    init {
        xSize = 255
        ySize = 255
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        val inventoryXOffset = (xSize - PlayerInventoryTexture.width) / 2
        val inventoryYOffset = ySize - PlayerInventoryTexture.height

        PlayerInventoryTexture.draw(
            origin = originMatchingSlots(),
            zLevel = this.zLevel
        )
    }

    private fun originMatchingSlots() =
        Vector(guiLeft + 40, guiTop + 168)
}

open class ResearchTableSlotContainer constructor(
    private val tileEntity: TileResearchTable,
    private val specializedSlotIndexRange: IntRange
) : Container() {

    companion object {
        private const val SCRIBE_TOOLS_SLOT_INDEX = 0
        private const val RESEARCH_NOTES_SLOT_INDEX = 1

        fun create(
            playerInventory: InventoryPlayer,
            tableTileEntity: TileResearchTable,
            scribeToolsSlotOffset: Vector = Vector(14, 10),
            notesSlotOffset: Vector = Vector(70, 10),
            inventorySlotOffset: Vector = Vector(48, 175)
        ) = ResearchTableSlotContainer(tableTileEntity, specializedSlotIndexRange = 0..1).apply {
            addSlotToContainer(specializedOn<IScribeTools>(tableTileEntity, SCRIBE_TOOLS_SLOT_INDEX, scribeToolsSlotOffset.x, scribeToolsSlotOffset.y))
            addSlotToContainer(specializedOn<ItemResearchNotes>(tableTileEntity, RESEARCH_NOTES_SLOT_INDEX, notesSlotOffset.x, notesSlotOffset.y))
            addPlayerInventorySlots(playerInventory, inventorySlotOffset)
        }

        private fun ResearchTableSlotContainer.addPlayerInventorySlots(
            playerInventory: InventoryPlayer,
            inventorySlotOffset: Vector
        ) {
            addInternalInventorySlots(playerInventory, inventorySlotOffset)
            addHobartSlots(playerInventory, inventorySlotOffset)
        }

        private fun ResearchTableSlotContainer.addInternalInventorySlots(
            playerInventory: InventoryPlayer,
            inventorySlotOffset: Vector
        ) {
            for (rowIndex in internalRowIndexes) {
                for (columnIndex in columnIndexes) {
                    val inventoryIndex = columnIndex + rowIndex * SLOTS_IN_INVENTORY_ROW + SLOTS_IN_INVENTORY_ROW
                    val x = inventorySlotOffset.x + columnIndex * SLOT_SIZE_PIXELS
                    val y = inventorySlotOffset.y + rowIndex * SLOT_SIZE_PIXELS
                    addSlotToContainer(Slot(playerInventory, inventoryIndex, x, y))
                }
            }
        }

        private fun ResearchTableSlotContainer.addHobartSlots(
            playerInventory: InventoryPlayer,
            inventorySlotOffset: Vector
        ) {
            for (columnIndex in columnIndexes) {
                val x = inventorySlotOffset.x + columnIndex * SLOT_SIZE_PIXELS
                val y = inventorySlotOffset.y + HOTBAR_ROW_INDEX * SLOT_SIZE_PIXELS + HOTBAR_ROW_DELIMITER_HEIGHT_PIXELS
                addSlotToContainer(Slot(playerInventory, columnIndex, x, y))
            }
        }
    }

    override fun enchantItem(par1EntityPlayer: EntityPlayer, button: Int): Boolean {
        if (button == 1) {
            return true
        }
        if (button == 5) {
            tileEntity.duplicate(par1EntityPlayer)
            return true
        }
        return false
    }

    /**
     * Avoiding stack overflow crash on shift-click.
     */
    override fun transferStackInSlot(player: EntityPlayer, slotIndex: Int): ItemStack? {
        val slot: Slot = inventorySlots[slotIndex] as Slot
        if(slot.hasStack) {
            val stack = slot.stack
            val stackCopy = stack.copy()
            if (slotIndex in specializedSlotIndexRange) {
                if (!transferFromSpecializedSlot(stack)) {
                    return null
                }
            } else if (!transferToSpecializedSlot(stack)) {
                return null
            }
            if (stack.stackSize == 0) {
                slot.putStack(null)
            } else {
                slot.onSlotChanged()
            }
            return stackCopy
        }
        return null
    }

    private fun transferFromSpecializedSlot(stack: ItemStack) =
        mergeItemStack(stack, specializedSlotIndexRange.count(), inventorySlots.size, true)

    private fun transferToSpecializedSlot(stack: ItemStack) =
        mergeItemStack(stack, specializedSlotIndexRange.first, specializedSlotIndexRange.count(), false)

    /**
     * Copy-paste from original Container with addition of isItemValid check
     */
    override fun mergeItemStack(sourceStack: ItemStack, startIndex: Int, endIndex: Int, reverseDirection: Boolean): Boolean {
        var mergeSuccess = false
        var begining = startIndex
        if (reverseDirection) {
            begining = endIndex - 1
        }
        if (sourceStack.isStackable) {
            while (sourceStack.stackSize > 0 && (!reverseDirection && begining < endIndex || reverseDirection && begining >= startIndex)) {
                val targetSlot: Slot = inventorySlots[begining] as Slot
                val targetStack = targetSlot.stack
                if (
                    targetStack != null && targetSlot.isItemValid(sourceStack) && targetStack.item === sourceStack.item
                    && (!sourceStack.hasSubtypes || sourceStack.itemDamage == targetStack.itemDamage)
                    && ItemStack.areItemStackTagsEqual(sourceStack, targetStack)
                ) {
                    val targetStackSize = targetStack.stackSize + sourceStack.stackSize
                    if (targetStackSize <= sourceStack.maxStackSize) {
                        sourceStack.stackSize = 0
                        targetStack.stackSize = targetStackSize
                        targetSlot.onSlotChanged()
                        mergeSuccess = true
                    } else if (targetStack.stackSize < sourceStack.maxStackSize) {
                        sourceStack.stackSize -= sourceStack.maxStackSize - targetStack.stackSize
                        targetStack.stackSize = sourceStack.maxStackSize
                        targetSlot.onSlotChanged()
                        mergeSuccess = true
                    }
                }
                if (reverseDirection) {
                    --begining
                } else {
                    ++begining
                }
            }
        }
        if (sourceStack.stackSize > 0) {
            begining = if (reverseDirection) {
                endIndex - 1
            } else {
                startIndex
            }
            while (!reverseDirection && begining < endIndex || reverseDirection && begining >= startIndex) {
                val targetSlot: Slot = inventorySlots[begining] as Slot
                val targetStack = targetSlot.stack
                if (targetStack == null && targetSlot.isItemValid(sourceStack)) {
                    targetSlot.putStack(sourceStack.copy())
                    targetSlot.onSlotChanged()
                    sourceStack.stackSize = 0
                    mergeSuccess = true
                    break
                }
                if (reverseDirection) {
                    --begining
                } else {
                    ++begining
                }
            }
        }
        return mergeSuccess
    }

    override fun canInteractWith(player: EntityPlayer): Boolean {
        return tileEntity.isUseableByPlayer(player)
    }
}

class SpecializedSlot(
    private val targetClass: Class<*>,
    inventory: IInventory, slotIndex: Int,
    xDisplayPosition: Int, yDisplayPosition: Int
) : Slot(inventory, slotIndex, xDisplayPosition, yDisplayPosition) {

    override fun isItemValid(stack: ItemStack): Boolean {
        return stack.item != null && targetClass.isAssignableFrom(stack.item.javaClass)
    }

    companion object {
        inline fun <reified TargetClassT> specializedOn(
            inventory: IInventory, slotIndex: Int,
            xDisplayPosition: Int, yDisplayPosition: Int
        ) =
            SpecializedSlot(TargetClassT::class.java, inventory, slotIndex, xDisplayPosition, yDisplayPosition)
    }

}

