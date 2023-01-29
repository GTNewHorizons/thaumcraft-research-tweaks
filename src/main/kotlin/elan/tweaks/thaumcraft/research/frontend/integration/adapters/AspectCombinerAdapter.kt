package elan.tweaks.thaumcraft.research.frontend.integration.adapters

import elan.tweaks.common.ext.ResultExt.success
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.AspectCombiner
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.api.aspects.Aspect
import thaumcraft.common.lib.network.PacketHandler
import thaumcraft.common.lib.network.playerdata.PacketAspectCombinationToServer
import thaumcraft.common.tiles.TileResearchTable

class AspectCombinerAdapter(
    private val player: EntityPlayer,
    private val table: TileResearchTable
) : AspectCombiner {
  override fun combine(firstAspect: Aspect, secondAspect: Aspect): Result<Unit> {
    val combineAspectsPacket =
        PacketAspectCombinationToServer(
            player,
            table.xCoord,
            table.yCoord,
            table.zCoord,
            firstAspect,
            secondAspect,
            table.bonusAspects.getAmount(firstAspect) > 0,
            table.bonusAspects.getAmount(secondAspect) > 0,
            true)
    PacketHandler.INSTANCE.sendToServer(combineAspectsPacket)
    return success()
  }
}
