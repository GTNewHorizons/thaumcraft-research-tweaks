package elan.tweaks.thaumcraft.research.integration

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLInitializationEvent
import elan.tweaks.thaumcraft.research.integration.Main.DEPENDENCIES
import elan.tweaks.thaumcraft.research.integration.Main.MOD_ID
import elan.tweaks.thaumcraft.research.integration.Main.MOD_LANGUAGE_ADAPTER
import elan.tweaks.thaumcraft.research.integration.Main.NAME
import elan.tweaks.thaumcraft.research.integration.Main.VERSION
import net.minecraft.init.Blocks

@Mod(
    modid = MOD_ID,
    name = NAME,
    version = VERSION,
    dependencies = DEPENDENCIES,
    modLanguageAdapter = MOD_LANGUAGE_ADAPTER
)
object Main {
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent?) {
        // some example code
        println("DIRT BLOCK >> " + Blocks.dirt.unlocalizedName)
    }

    const val MOD_ID = "thaumcraftresearchtweaks"
    const val NAME = "Thaumcraft NEI Additions"
    const val VERSION = "@GRADLE_PROJECT_VERSION@"
    const val MOD_LANGUAGE_ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
    const val DEPENDENCIES = "required-after:forgelin;" +
            "required-after:spongemixins;" +
            "required-after:Thaumcraft;"
}
