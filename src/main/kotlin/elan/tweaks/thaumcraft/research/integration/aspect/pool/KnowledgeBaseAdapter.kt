package elan.tweaks.thaumcraft.research.integration.aspect.pool

import elan.tweaks.thaumcraft.research.domain.ports.spi.KnowledgeBase
import thaumcraft.common.lib.research.ResearchManager

class KnowledgeBaseAdapter(
    private val playerCommandSenderName: String
) : KnowledgeBase {
    companion object {
        private const val RESEARCH_MASTERY = "RESEARCHER2"
    }
    
    override fun notDiscoveredResearchMastery(): Boolean =
        ResearchManager.isResearchComplete(playerCommandSenderName, RESEARCH_MASTERY)

}
