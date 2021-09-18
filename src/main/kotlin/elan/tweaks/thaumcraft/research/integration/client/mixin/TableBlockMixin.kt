package elan.tweaks.thaumcraft.research.integration.client.mixin

import elan.tweaks.thaumcraft.research.integration.ThaumcraftResearchTweaks
import elan.tweaks.thaumcraft.research.integration.client.gui.GuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.*
import thaumcraft.common.blocks.BlockTable

@Mixin(BlockTable::class)
abstract class TableBlockMixin {

    private companion object {
        private const val THAUMCRAFT_RESEARCH_TABLE_GUI_ID = 10
    }

    @Redirect(
        method = ["onBlockActivated"],
        at = At(value = "INVOKE", target = "net.minecraft.entity.player.EntityPlayer.openGui(Ljava/lang/Object;ILnet/minecraft/world/World;III)V"),
        require = 4
    )
    private fun correctGuiCallFor(entityPlayer: EntityPlayer, mod: Any, modGuiId: Int, world: World, x: Int, y: Int, z: Int) {
        return if (modGuiId == THAUMCRAFT_RESEARCH_TABLE_GUI_ID) {
            println("Calling new gui for guiId: $modGuiId")
            entityPlayer.openGui(ThaumcraftResearchTweaks, GuiHandler.IDs.RESEARCH_TABLE, world, x, y, z)
        } else {
            println("Calling original gui for guiId: $modGuiId")
            entityPlayer.openGui(mod, modGuiId, world, x, y, z)
        }
    }


}
