package elan.tweaks.thaumcraft.research.integration.aspect.pool

import elan.tweaks.common.ext.ResultExt.success
import elan.tweaks.thaumcraft.research.domain.ports.spi.AspectCombiner
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.api.aspects.Aspect
import thaumcraft.common.lib.network.PacketHandler
import thaumcraft.common.lib.network.playerdata.PacketAspectCombinationToServer
import thaumcraft.common.tiles.TileResearchTable

class AspectCombinerAdapter(
    private val player: EntityPlayer,
    private val tableEntity: TileResearchTable
) : AspectCombiner {
    override fun combine(firstAspect: Aspect, secondAspect: Aspect): Result<Unit> {
        val combineAspectsPacket = PacketAspectCombinationToServer(
            player,
            tableEntity.xCoord, tableEntity.yCoord, tableEntity.zCoord,
            firstAspect, secondAspect,
            tableEntity.bonusAspects.getAmount(firstAspect) > 0,
            tableEntity.bonusAspects.getAmount(secondAspect) > 0,
            true
        )
        PacketHandler.INSTANCE.sendToServer(combineAspectsPacket)
        return success()
    }

}
