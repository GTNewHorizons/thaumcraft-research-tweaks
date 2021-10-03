package elan.tweaks.thaumcraft.research.frontend.domain.failures

import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.KnowledgeBase

class MissingResearchFailure(message: String) : Exception(message) {
    companion object {
        fun <ResultT> missingResearchMastery() = Result.failure<ResultT>(
            MissingResearchFailure("${KnowledgeBase.RESEARCH_MASTERY} is required")
        )
    }
}
