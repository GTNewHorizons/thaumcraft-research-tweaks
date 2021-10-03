package elan.tweaks.thaumcraft.research.frontend.integration.adapters

import elan.tweaks.common.ext.ResultExt.success
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.ResearchNotes
import elan.tweaks.thaumcraft.research.frontend.integration.failures.HexNotFoundFailure.Companion.screenHexNotFound
import elan.tweaks.thaumcraft.research.frontend.integration.table.container.ResearchTableContainerFactory.RESEARCH_NOTES_SLOT_INDEX
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.research.ResearchCategories
import thaumcraft.common.lib.network.PacketHandler
import thaumcraft.common.lib.network.playerdata.PacketAspectPlaceToServer
import thaumcraft.common.lib.research.ResearchManager
import thaumcraft.common.tiles.TileResearchTable

class ResearchNotesAdapter(
    private val player: EntityPlayer,
    private val table: TileResearchTable,
) : ResearchNotes {
    override val present: Boolean
        get() = notes != null

    override val complete: Boolean
        get() = notes?.stackTagCompound?.getBoolean("complete") ?: false

    private val notes: ItemStack?
        get() =
            table.getStackInSlot(RESEARCH_NOTES_SLOT_INDEX)

    override fun erase(hexKey: String): Result<Unit> = 
        sendAspectPlacePacket(hexKey, aspect = null)

    override fun write(hexKey: String, aspect: Aspect): Result<Unit> = 
        sendAspectPlacePacket(hexKey, aspect)

    private fun sendAspectPlacePacket(hexKey: String, aspect: Aspect?): Result<Unit> {
        val screenHex = getResearchData().hexes[hexKey] ?: return screenHexNotFound(hexKey)

        PacketHandler.INSTANCE.sendToServer(
            PacketAspectPlaceToServer(
                player,
                screenHex.q.toByte(), screenHex.r.toByte(),
                table.xCoord, table.yCoord, table.zCoord,
                aspect
            )
        )

        return success()
    }

    override fun findUsedAspectAmounts(): Map<Aspect, Int> {
        val researchData = getResearchData()
        val aspects = ResearchCategories.getResearch(researchData.key).tags
        
        return aspects.getAspects().associateWith { aspect -> researchData.copies + aspects.getAmount(aspect) }
    }


    private fun getResearchData() =
        ResearchManager.getData(
            table.getStackInSlot(RESEARCH_NOTES_SLOT_INDEX)
        )
}
