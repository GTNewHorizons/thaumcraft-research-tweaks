package elan.tweaks.thaumcraft.research.frontend.integration.adapters

import elan.tweaks.common.ext.ResultExt.success
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.ResearchNotes
import elan.tweaks.thaumcraft.research.frontend.integration.failures.HexNotFoundFailure.Companion.screenHexNotFound
import elan.tweaks.thaumcraft.research.frontend.integration.table.container.ResearchTableContainerFactory.DUPLICATE_ACTION_ID
import elan.tweaks.thaumcraft.research.frontend.integration.table.container.ResearchTableContainerFactory.RESEARCH_NOTES_SLOT_INDEX
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.research.ResearchCategories
import thaumcraft.common.lib.network.PacketHandler
import thaumcraft.common.lib.network.playerdata.PacketAspectPlaceToServer
import thaumcraft.common.lib.research.ResearchManager
import thaumcraft.common.lib.research.ResearchNoteData
import thaumcraft.common.tiles.TileResearchTable

class ResearchNotesAdapter(
    private val player: EntityPlayer,
    private val table: TileResearchTable,
    private val container: Container
) : ResearchNotes {
  override val present: Boolean
    get() = notes != null

  override val complete: Boolean
    get() = notes?.stackTagCompound?.getBoolean("complete") ?: false

  override val valid: Boolean
    get() = validateData()

  override val data: ResearchNoteData?
    get() = notes?.let(ResearchManager::getData)

  private val notes: ItemStack?
    get() = table.getStackInSlot(RESEARCH_NOTES_SLOT_INDEX)

  private fun validateData(): Boolean {
    val hexes = data?.hexEntries?.values ?: return true
    return hexes.all(this::nonVacantHexesHaveAspects)
  }

  private fun nonVacantHexesHaveAspects(hex: ResearchManager.HexEntry) =
      hex.type == HexType.VACANT || hex.aspect != null

  override fun erase(hexKey: String): Result<Unit> = sendAspectPlacePacket(hexKey, aspect = null)

  override fun write(hexKey: String, aspect: Aspect): Result<Unit> =
      sendAspectPlacePacket(hexKey, aspect)

  override fun duplicate(): Result<Unit> {
    Minecraft.getMinecraft()
        .playerController
        .sendEnchantPacket(container.windowId, DUPLICATE_ACTION_ID)

    return success()
  }

  private fun sendAspectPlacePacket(hexKey: String, aspect: Aspect?): Result<Unit> {
    val screenHex = getResearchData().hexes[hexKey] ?: return screenHexNotFound(hexKey)

    PacketHandler.INSTANCE.sendToServer(
        PacketAspectPlaceToServer(
            player,
            screenHex.q.toByte(),
            screenHex.r.toByte(),
            table.xCoord,
            table.yCoord,
            table.zCoord,
            aspect))

    return success()
  }

  override fun findUsedAspectAmounts(): Map<Aspect, Int> {
    val researchData = getResearchData()
    val aspects = ResearchCategories.getResearch(researchData.key).tags

    return aspects.getAspects().associateWith { aspect ->
      researchData.copies + aspects.getAmount(aspect)
    }
  }

  private fun getResearchData() =
      ResearchManager.getData(table.getStackInSlot(RESEARCH_NOTES_SLOT_INDEX))

  object HexType {
    const val VACANT = 0
    const val ROOT = 1
    const val NODE = 2
  }
}
