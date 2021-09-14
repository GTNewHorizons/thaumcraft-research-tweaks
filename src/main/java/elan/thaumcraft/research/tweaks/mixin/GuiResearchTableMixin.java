package elan.thaumcraft.research.tweaks.mixin;

import net.minecraft.client.gui.inventory.*;
import net.minecraft.inventory.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import thaumcraft.client.gui.*;

@Mixin(GuiResearchTable.class)
public abstract class GuiResearchTableMixin extends GuiContainer {

  public GuiResearchTableMixin(Container p_i1072_1_) {
    super(p_i1072_1_);
  }

  @Inject(method = "drawAspects", at = @At("HEAD"), remap = false)
  private void onDrawAspects(int x, int y, CallbackInfo ci) {
    System.out.println("INJECTION WORKED x: " + x + ", y: " + y);
  }

}
