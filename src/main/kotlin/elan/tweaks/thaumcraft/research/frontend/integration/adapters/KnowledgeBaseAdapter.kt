package elan.tweaks.thaumcraft.research.frontend.integration.adapters

import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.KnowledgeBase
import thaumcraft.common.lib.research.ResearchManager

class KnowledgeBaseAdapter(
    private val playerCommandSenderName: String
) : KnowledgeBase {
    companion object {
        private const val RESEARCH_EXPERTISE = "RESEARCHER1"
        private const val RESEARCH_MASTERY = "RESEARCHER2"
        private const val RESEARCH_DUPLICATION = "RESEARCHDUPE"
    }

    override fun notDiscoveredResearchExpertise(): Boolean =
        !hasDiscovered(RESEARCH_EXPERTISE)

    override fun notDiscoveredResearchMastery(): Boolean =
        !hasDiscovered(RESEARCH_MASTERY)

    override fun notDiscoveredResearchDuplication(): Boolean =
        !hasDiscovered(RESEARCH_DUPLICATION)

    private fun hasDiscovered(researchName: String) = 
        ResearchManager.isResearchComplete(playerCommandSenderName, researchName)

}
