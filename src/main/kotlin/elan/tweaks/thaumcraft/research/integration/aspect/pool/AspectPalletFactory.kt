package elan.tweaks.thaumcraft.research.integration.aspect.pool

import elan.tweaks.thaumcraft.research.domain.model.AspectPallet
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.common.tiles.TileResearchTable

object AspectPalletFactory {
    fun create(player: EntityPlayer, tableEntity: TileResearchTable) =
        AspectPallet(
            pool = AspectPoolAdapter(player, tableEntity),
            knowledge = KnowledgeBaseAdapter(playerCommandSenderName = player.commandSenderName),
            combiner = AspectCombinerAdapter(player, tableEntity)
        )
}
