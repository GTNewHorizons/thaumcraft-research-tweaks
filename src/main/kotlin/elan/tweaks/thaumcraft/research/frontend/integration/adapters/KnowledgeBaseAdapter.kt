package elan.tweaks.thaumcraft.research.frontend.integration.adapters

import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort.Knowledge
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.KnowledgeBase
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.common.lib.research.ResearchManager

class KnowledgeBaseAdapter(private val player: EntityPlayer) : KnowledgeBase {
  companion object {
    private val knowledgeToResearchKey =
        mapOf(
            Knowledge.ResearchDuplication to "RESEARCHDUPE",
            Knowledge.ResearchExpertise to "RESEARCHER1",
            Knowledge.ResearchMastery to "RESEARCHER2",
        )
  }

  override fun hasDiscovered(knowledge: Knowledge): Boolean =
      knowledgeToResearchKey.containsKey(knowledge) &&
          hasDiscovered(knowledgeToResearchKey[knowledge]!!)

  private fun hasDiscovered(researchName: String) =
      ResearchManager.isResearchComplete(player.commandSenderName, researchName)
}
