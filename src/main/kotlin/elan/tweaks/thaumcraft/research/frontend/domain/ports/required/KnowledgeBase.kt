package elan.tweaks.thaumcraft.research.frontend.domain.ports.required

import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort.Knowledge

interface KnowledgeBase {
  fun hasNotDiscovered(knowledge: Knowledge): Boolean = !hasDiscovered(knowledge)
  fun hasDiscovered(knowledge: Knowledge): Boolean

  companion object {
    const val RESEARCH_MASTERY = "research mastery"
    const val RESEARCH_DUPLICATION = "research duplication"
  }
}
