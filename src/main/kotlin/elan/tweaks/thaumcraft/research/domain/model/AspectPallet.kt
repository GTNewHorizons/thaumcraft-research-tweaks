package elan.tweaks.thaumcraft.research.domain.model

import elan.tweaks.thaumcraft.research.domain.failures.AspectCombinationFailure.Companion.cannotCombinePrimalAspect
import elan.tweaks.thaumcraft.research.domain.failures.AspectCombinationFailure.Companion.missingComponents
import elan.tweaks.thaumcraft.research.domain.failures.MissingResearchFailure.Companion.missingResearchMastery
import elan.tweaks.thaumcraft.research.domain.ports.provided.AspectPalletPort
import elan.tweaks.thaumcraft.research.domain.ports.required.AspectCombiner
import elan.tweaks.thaumcraft.research.domain.ports.required.AspectPool
import elan.tweaks.thaumcraft.research.domain.ports.required.KnowledgeBase
import thaumcraft.api.aspects.Aspect


class AspectPallet(
    private val knowledge: KnowledgeBase,
    private val pool: AspectPool,
    private val combiner: AspectCombiner,

    private val batchSize: Int = 10 // TODO: Extract to config
) : AspectPalletPort {

    override fun isDrainedOf(aspect: Aspect): Boolean =
        pool.totalAmountOf(aspect) <= 0

    override fun amountAndBonusOf(aspect: Aspect): Pair<Int, Int> =
        pool.amountOf(aspect) to pool.bonusAmountOf(aspect)

    override fun discoveredAspects(): Array<Aspect> =
        pool.discovered()

    override fun deriveBatch(desiredAspect: Aspect): Result<Unit> =
        batch {
            derive(desiredAspect)
        }

    override fun derive(desiredAspect: Aspect): Result<Unit> =
        when {
            knowledge.notDiscoveredResearchMastery() -> missingResearchMastery()
            desiredAspect.isPrimal -> cannotCombinePrimalAspect()
            pool.anyComponentMissingFor(desiredAspect) -> missingComponents()
            else -> combiner.combine(desiredAspect.components[0], desiredAspect.components[1])
        }

    override fun combineBatch(firstAspect: Aspect, secondAspect: Aspect): Result<Unit> =
        if(knowledge.notDiscoveredResearchExpertise()) combine(firstAspect, secondAspect)
        else batch {
            combine(firstAspect, secondAspect)
        }

    override fun combine(firstAspect: Aspect, secondAspect: Aspect): Result<Unit> =
        when {
            isDrainedOf(firstAspect) || isDrainedOf(secondAspect) -> missingComponents()
            else -> combiner.combine(firstAspect, secondAspect)
        }

    private fun <ResultT> batch(function: () -> Result<ResultT>): Result<ResultT> {
        val batchResults = (1..batchSize).map { function() }

        return batchResults.firstOrNull { it.isSuccess } ?: batchResults.first()
    }
}

