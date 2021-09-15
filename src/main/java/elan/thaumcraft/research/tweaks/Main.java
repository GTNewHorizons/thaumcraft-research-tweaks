package elan.thaumcraft.research.tweaks;

import static elan.thaumcraft.research.tweaks.Main.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.*;
import net.minecraft.init.*;

@Mod(
    modid = MOD_ID, name = NAME, version = VERSION,
    dependencies = "required-after:Thaumcraft;required-after:spongemixins@[1.1.0,);"
)
public class Main {

    public static final String MOD_ID = "thaumcraftresearchtweaks";
    public static final String NAME = "Thaumcraft NEI Additions";
    public static final String VERSION = "GRADLETOKEN_VERSION";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        System.out.println("DIRT BLOCK >> " + Blocks.dirt.getUnlocalizedName());
    }
}
