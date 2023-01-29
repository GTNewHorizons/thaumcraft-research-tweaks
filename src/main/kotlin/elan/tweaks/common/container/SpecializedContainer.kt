package elan.tweaks.common.container

import elan.tweaks.common.gui.dto.Vector2D
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class SpecializedContainer
constructor(
    private val onEnchantItem: (EntityPlayer, Int) -> Boolean,
    private val onCanInteractWith: (EntityPlayer) -> Boolean,
    /** Should start from 0 for transfer stack function to work correctly */
    private val specializedSlotIndexRange: IntRange,
) : Container() {

  override fun enchantItem(player: EntityPlayer, id: Int): Boolean = onEnchantItem(player, id)
  override fun canInteractWith(player: EntityPlayer): Boolean = onCanInteractWith(player)

  public override fun addSlotToContainer(slot: Slot) = super.addSlotToContainer(slot)!!

  /** Avoiding stack overflow crash on shift-click. */
  override fun transferStackInSlot(player: EntityPlayer, slotIndex: Int): ItemStack? {
    val slot: Slot = inventorySlots[slotIndex] as Slot
    if (slot.hasStack) {
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
      mergeItemStack(
          stack, specializedSlotIndexRange.first, specializedSlotIndexRange.count(), false)

  /** Copy-paste from original Container with addition of isItemValid check */
  override fun mergeItemStack(
      sourceStack: ItemStack,
      startIndex: Int,
      endIndex: Int,
      reverseDirection: Boolean
  ): Boolean {
    var mergeSuccess = false
    var begining = startIndex
    if (reverseDirection) {
      begining = endIndex - 1
    }
    if (sourceStack.isStackable) {
      while (sourceStack.stackSize > 0 &&
          (!reverseDirection && begining < endIndex ||
              reverseDirection && begining >= startIndex)) {
        val targetSlot: Slot = inventorySlots[begining] as Slot
        val targetStack = targetSlot.stack
        if (targetStack != null &&
            targetSlot.isItemValid(sourceStack) &&
            targetStack.item === sourceStack.item &&
            (!sourceStack.hasSubtypes || sourceStack.itemDamage == targetStack.itemDamage) &&
            ItemStack.areItemStackTagsEqual(sourceStack, targetStack)) {
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
      begining =
          if (reverseDirection) {
            endIndex - 1
          } else {
            startIndex
          }
      while (!reverseDirection && begining < endIndex ||
          reverseDirection && begining >= startIndex) {
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

  class SpecializedSlot(
      private val targetClass: Class<*>,
      inventory: IInventory,
      slotIndex: Int,
      displayPosition: Vector2D
  ) : Slot(inventory, slotIndex, displayPosition.x, displayPosition.y) {

    override fun isItemValid(stack: ItemStack): Boolean {
      return stack.item != null && targetClass.isAssignableFrom(stack.item.javaClass)
    }

    companion object {
      inline fun <reified TargetClassT> specializedOn(
          inventory: IInventory,
          slotIndex: Int,
          displayPosition: Vector2D
      ) = SpecializedSlot(TargetClassT::class.java, inventory, slotIndex, displayPosition)
    }
  }
}
