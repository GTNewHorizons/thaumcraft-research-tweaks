package elan.tweaks.thaumcraft.research.integration.adapters

import elan.tweaks.thaumcraft.research.domain.ports.required.AspectPool
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.api.aspects.Aspect
import thaumcraft.common.Thaumcraft
import thaumcraft.common.tiles.TileResearchTable

class AspectPoolAdapter(
    private val player: EntityPlayer,
    private val table: TileResearchTable
) : AspectPool {
    override fun discovered(): Array<Aspect> =
        playerAspectList()
            .getAspects()

    override fun totalAmountOf(aspect: Aspect): Int =
        amountOf(aspect) + bonusAmountOf(aspect)

    override fun anyComponentMissingFor(aspect: Aspect): Boolean {
        val components = aspect.components ?: return false
        return aspect.isCompound && components.size == 2
                && totalAmountOf(components[0]) <= 0
                && totalAmountOf(components[1]) <= 0
    }

    private val Aspect.isCompound get() = !isPrimal

    override fun amountOf(aspect: Aspect): Int =
        playerAspectList().getAmount(aspect)

    private fun playerAspectList() =
        Thaumcraft.proxy
            .getPlayerKnowledge()
            .getAspectsDiscovered(player.commandSenderName)

    override fun bonusAmountOf(aspect: Aspect): Int =
        table.bonusAspects.getAmount(aspect)

}
