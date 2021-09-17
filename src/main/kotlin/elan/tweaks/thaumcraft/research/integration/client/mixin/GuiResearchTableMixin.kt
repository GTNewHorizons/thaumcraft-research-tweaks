package elan.tweaks.thaumcraft.research.integration.client.mixin

import net.minecraft.client.gui.Gui
import net.minecraft.entity.player.EntityPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.gui.GuiResearchTable
import thaumcraft.client.lib.UtilsFX
import thaumcraft.common.Thaumcraft
import thaumcraft.common.tiles.TileResearchTable


@Mixin(GuiResearchTable::class)
abstract class GuiResearchTableMixin : Gui() {
  @Shadow(remap = false)
  private var lastPage = 0

  @Shadow(remap = false)
  private val username: String? = null

  @Shadow(remap = false)
  private val page = 0

  @Shadow(remap = false)
  private val tileEntity: TileResearchTable? = null

  @Shadow(remap = false)
  var select1: Aspect? = null

  @Shadow(remap = false)
  var select2: Aspect? = null

  @Shadow(remap = false)
  var player: EntityPlayer? = null
  
  private val aspectsPerPage = 10

  @Inject(method = ["drawAspects"], at = [At("HEAD")], remap = false, require = 1, cancellable = true)
  private fun onDrawAspects(x: Int, y: Int, ci: CallbackInfo) {
    val aspects = Thaumcraft.proxy.getPlayerKnowledge().getAspectsDiscovered(username)
    if (aspects != null) {
      var count = aspects.size()
      lastPage = (count - 20) / aspectsPerPage
      count = 0
      var drawn = 0
      val aspectsSorted = aspects.aspectsSorted
      val aspectSize = aspectsSorted.size
      for (aspectIndex in 0 until aspectSize) {
        val aspect = aspectsSorted[aspectIndex]
        ++count
        if (count - 1 >= page * aspectsPerPage && drawn < 25) {
          val faded = aspects.getAmount(aspect) <= 0 && tileEntity!!.bonusAspects.getAmount(aspect) <= 0
          val xx = drawn / aspectsPerPage * 16
          val yy = drawn % aspectsPerPage * 16
          UtilsFX.drawTag(
            x + xx, y + yy, aspect, aspects.getAmount(aspect).toFloat(), tileEntity!!.bonusAspects.getAmount(aspect), zLevel.toDouble(), 771,
            if (faded) 0.33f else 1.0f
          )
          ++drawn
        }
      }
    }
    if (select1 != null && Thaumcraft.proxy.playerKnowledge.getAspectPoolFor(player!!.commandSenderName, select1) <= 0 && tileEntity!!.bonusAspects.getAmount(
        select1
      ) <= 0
    ) {
      select1 = null
    }
    if (select2 != null && Thaumcraft.proxy.playerKnowledge.getAspectPoolFor(player!!.commandSenderName, select2) <= 0 && tileEntity!!.bonusAspects.getAmount(
        select2
      ) <= 0
    ) {
      select2 = null
    }
    if (select1 != null) {
      UtilsFX.drawTag(x + 3, y + 99, select1, 0.0f, 0, zLevel.toDouble())
    }
    if (select2 != null) {
      UtilsFX.drawTag(x + 61, y + 99, select2, 0.0f, 0, zLevel.toDouble())
    }
    ci.cancel()
  }
}
