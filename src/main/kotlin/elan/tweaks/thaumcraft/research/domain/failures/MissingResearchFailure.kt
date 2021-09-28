package elan.tweaks.thaumcraft.research.domain.failures

import elan.tweaks.thaumcraft.research.domain.ports.spi.KnowledgeBase

class MissingResearchFailure(message: String) : Exception(message) {
    companion object {
        fun <ResultT> missingResearchMastery() = Result.failure<ResultT>(
            MissingResearchFailure("${KnowledgeBase.RESEARCH_MASTERY} is required")
        )
    }
}
