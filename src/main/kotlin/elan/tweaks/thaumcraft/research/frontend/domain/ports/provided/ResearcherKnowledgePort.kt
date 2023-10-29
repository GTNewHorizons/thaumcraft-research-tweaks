package elan.tweaks.thaumcraft.research.frontend.domain.ports.provided

import thaumcraft.api.aspects.Aspect

interface ResearcherKnowledgePort {
  fun hasDiscovered(knowledge: Knowledge): Boolean
  fun hasDiscovered(aspect: Aspect): Boolean
  fun allDiscoveredAspects(): Array<Aspect>

  enum class Knowledge {
    ResearchExpertise,
    ResearchMastery,
    ResearchDuplication,
  }
}
