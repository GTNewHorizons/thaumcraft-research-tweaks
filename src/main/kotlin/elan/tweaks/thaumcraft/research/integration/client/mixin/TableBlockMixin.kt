package elan.tweaks.thaumcraft.research.integration.client.mixin

import elan.tweaks.thaumcraft.research.integration.ThaumcraftResearchTweaks
import elan.tweaks.thaumcraft.research.integration.client.gui.GuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.ModifyArg
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import thaumcraft.common.blocks.BlockTable

@Mixin(BlockTable::class)
abstract class TableBlockMixin {

    private companion object {
        private const val THAUMCRAFT_RESEARCH_TABLE_GUI_ID = 10
    }

    @ModifyArg(
        method = ["onBlockActivated"],
        at = At(value = "INVOKE", target = "net.minecraft.entity.player.EntityPlayer.openGui(Ljava/lang/Object;ILnet/minecraft/world/World;III)V"),
        index = 1,
        require = 4
    )
    private fun correctModGuiIdFor(modGuiId: Int): Int {
        println("correctModGuiIdFor($modGuiId)")
        return if (modGuiId == THAUMCRAFT_RESEARCH_TABLE_GUI_ID) {
            GuiHandler.IDs.RESEARCH_TABLE
        } else {
            modGuiId
        }
    }

    @ModifyArg(
        method = ["onBlockActivated"],
        at = At(value = "INVOKE", target = "net.minecraft.entity.player.EntityPlayer.openGui(Ljava/lang/Object;ILnet/minecraft/world/World;III)V"),
        index = 0,
        require = 4
    )
    private fun correctModFor(mod: Any, modGuiId: Int, world: World, x: Int, y: Int, z: Int): Any {
        println("correctModFor($mod, $modGuiId)")
        return if (modGuiId == THAUMCRAFT_RESEARCH_TABLE_GUI_ID) {
            ThaumcraftResearchTweaks
        } else {
            mod
        }
    }

    // I am not sure prevs are working...
//    @Inject(
//        method = ["onBlockActivated"],
//        at = [At(value = "INVOKE", target = "net.minecraft.entity.player.EntityPlayer.openGui(Ljava/lang/Object;ILnet/minecraft/world/World;III)V")],
//        require = 4,
//        cancellable = true
//    )
//    private fun openCorrectGui(player: EntityPlayer, mod: Any, modGuiId: Int, world: World, x: Int, y: Int, z: Int, callback: CallbackInfo) {
//        println("openCorrectGui($mod, $modGuiId)")
//        if (modGuiId == THAUMCRAFT_RESEARCH_TABLE_GUI_ID) {
//            println("Opening tweaked gui")
//            player.openGui(ThaumcraftResearchTweaks, GuiHandler.IDs.RESEARCH_TABLE, world, x, y, z)
//            callback.cancel()
//        } else {
//            println("Opening regular gui")
//        }
//    }
}
