package elan.tweaks.thaumcraft.research.frontend.domain.ports.required

interface KnowledgeBase {
    fun notDiscoveredResearchMastery(): Boolean
    fun notDiscoveredResearchExpertise(): Boolean
    fun notDiscoveredResearchDuplication(): Boolean

    companion object {
        const val RESEARCH_MASTERY = "research mastery"
    }
}
