package elan.tweaks.thaumcraft.research.frontend.integration.table.mixin

import elan.tweaks.thaumcraft.research.frontend.integration.ThaumcraftResearchTweaks
import elan.tweaks.thaumcraft.research.frontend.integration.table.ThaumcraftResearchGuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect
import thaumcraft.common.blocks.BlockTable

@Mixin(BlockTable::class)
abstract class TableBlockMixin {

  private val `tcrt$THAUMCRAFT_RESEARCH_TABLE_GUI_ID`: Int = 10

  @Redirect(
      method = ["onBlockActivated"],
      at =
          At(
              value = "INVOKE",
              target =
                  "net.minecraft.entity.player.EntityPlayer.openGui(Ljava/lang/Object;ILnet/minecraft/world/World;III)V",
              remap = false),
      require = 4)
  private fun correctGuiCallFor(
      entityPlayer: EntityPlayer,
      mod: Any,
      modGuiId: Int,
      world: World,
      x: Int,
      y: Int,
      z: Int
  ) {
    return if (modGuiId == `tcrt$THAUMCRAFT_RESEARCH_TABLE_GUI_ID`) {
      entityPlayer.openGui(
          ThaumcraftResearchTweaks, ThaumcraftResearchGuiHandler.IDs.RESEARCH_TABLE, world, x, y, z)
    } else {
      entityPlayer.openGui(mod, modGuiId, world, x, y, z)
    }
  }
}
