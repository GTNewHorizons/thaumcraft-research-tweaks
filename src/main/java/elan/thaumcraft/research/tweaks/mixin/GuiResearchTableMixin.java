package elan.thaumcraft.research.tweaks.mixin;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import thaumcraft.client.gui.*;

@Mixin(GuiResearchTable.class)
public abstract class GuiResearchTableMixin {

  @Inject(method = "drawAspects", at = @At("HEAD"), remap = false, require = 1)
  private void onDrawAspects(int x, int y, CallbackInfo ci) {
    System.out.println("INJECTION WORKED x: " + x + ", y: " + y);
  }

}
