package elan.tweaks.thaumcraft.research.frontend.integration.adapters

import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.PlayerInventory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import thaumcraft.common.lib.utils.InventoryUtils

class PlayerInventoryAdapter(private val player: EntityPlayer) : PlayerInventory {

  companion object {
    private val paperItemStack = ItemStack(Items.paper)
    private val blackDyeItemStack = ItemStack(Items.dye, 1, 0)
  }

  override fun containsPaper(): Boolean =
      InventoryUtils.isPlayerCarrying(player, paperItemStack) >= 0

  override fun containsBlackDye(): Boolean =
      InventoryUtils.isPlayerCarrying(player, blackDyeItemStack) >= 0
}
