package elan.thaumcraft.research.tweaks;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.*;
import net.minecraft.init.*;

@Mod(
    modid = Main.MOD_ID, 
    version = Main.VERSION, 
    dependencies = "required-after:spongemixins@[1.1.0,);"
)
public class Main {

    public static final String MOD_ID = "ThaumcraftResearchTweaks";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        System.out.println("DIRT BLOCK >> " + Blocks.dirt.getUnlocalizedName());
    }
}
