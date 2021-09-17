package elan.tweaks.thaumcraft.research.integration

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.network.IGuiHandler
import cpw.mods.fml.common.network.NetworkRegistry
import elan.tweaks.thaumcraft.research.integration.ThaumcraftResearchTweaks.DEPENDENCIES
import elan.tweaks.thaumcraft.research.integration.ThaumcraftResearchTweaks.MOD_ID
import elan.tweaks.thaumcraft.research.integration.ThaumcraftResearchTweaks.MOD_LANGUAGE_ADAPTER
import elan.tweaks.thaumcraft.research.integration.ThaumcraftResearchTweaks.NAME
import elan.tweaks.thaumcraft.research.integration.ThaumcraftResearchTweaks.VERSION
import elan.tweaks.thaumcraft.research.integration.client.gui.GuiHandler
import thaumcraft.common.Thaumcraft

@Mod(
    modid = MOD_ID,
    name = NAME,
    version = VERSION,
    dependencies = DEPENDENCIES,
    modLanguageAdapter = MOD_LANGUAGE_ADAPTER
)
object ThaumcraftResearchTweaks {
    
    
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(ThaumcraftResearchTweaks, GuiHandler())
    }

    const val MOD_ID = "thaumcraftresearchtweaks"
    const val NAME = "Thaumcraft NEI Additions"
    const val VERSION = "@GRADLE_PROJECT_VERSION@"
    const val MOD_LANGUAGE_ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
    const val DEPENDENCIES = "required-after:forgelin;" +
            "required-after:spongemixins;" +
            "required-after:Thaumcraft;"
}
