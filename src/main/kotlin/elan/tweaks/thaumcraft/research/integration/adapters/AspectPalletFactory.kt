package elan.tweaks.thaumcraft.research.integration.adapters

import elan.tweaks.thaumcraft.research.domain.model.AspectPallet
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.common.tiles.TileResearchTable

object AspectPalletFactory {
    fun create(player: EntityPlayer, table: TileResearchTable) =
        AspectPallet(
            pool = AspectPoolAdapter(player, table),
            knowledge = KnowledgeBaseAdapter(playerCommandSenderName = player.commandSenderName),
            combiner = AspectCombinerAdapter(player, table)
        )
}
