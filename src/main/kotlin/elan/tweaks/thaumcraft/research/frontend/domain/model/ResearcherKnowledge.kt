package elan.tweaks.thaumcraft.research.frontend.domain.model

import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.AspectPool
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.KnowledgeBase
import thaumcraft.api.aspects.Aspect

class ResearcherKnowledge
constructor(
    private val pool: AspectPool,
    private val base: KnowledgeBase,
) : ResearcherKnowledgePort {
  override fun hasDiscovered(knowledge: ResearcherKnowledgePort.Knowledge): Boolean =
      base.hasDiscovered(knowledge)

  override fun hasDiscovered(aspect: Aspect): Boolean = pool.hasDiscovered(aspect)

  override fun allDiscoveredAspects(): Array<Aspect> = pool.allDiscovered()
}
