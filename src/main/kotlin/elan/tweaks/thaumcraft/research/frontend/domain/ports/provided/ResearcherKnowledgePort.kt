package elan.tweaks.thaumcraft.research.frontend.domain.ports.provided

import thaumcraft.api.aspects.Aspect

interface ResearcherKnowledgePort {
    fun hasNotDiscovered(knowledge: Knowledge): Boolean = !hasDiscovered(knowledge)
    fun hasDiscovered(knowledge: Knowledge): Boolean
    fun hasNotDiscovered(aspect: Aspect): Boolean = !hasDiscovered(aspect)
    fun hasDiscovered(aspect: Aspect): Boolean
    fun allDiscoveredAspects(): Array<Aspect>
    
    enum class Knowledge {
        ResearchExpertise,
        ResearchMastery,
        ResearchDuplication,
    }
}
