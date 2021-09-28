package elan.tweaks.thaumcraft.research.domain.model

import elan.tweaks.thaumcraft.research.domain.failures.AspectCombinationFailure.Companion.cannotCombinePrimalAspect
import elan.tweaks.thaumcraft.research.domain.failures.AspectCombinationFailure.Companion.missingComponents
import elan.tweaks.thaumcraft.research.domain.failures.MissingResearchFailure.Companion.missingResearchMastery
import elan.tweaks.thaumcraft.research.domain.ports.api.AspectPalletPort
import elan.tweaks.thaumcraft.research.domain.ports.spi.AspectCombiner
import elan.tweaks.thaumcraft.research.domain.ports.spi.AspectPool
import elan.tweaks.thaumcraft.research.domain.ports.spi.KnowledgeBase
import thaumcraft.api.aspects.Aspect


class AspectPallet(
    private val knowledge: KnowledgeBase,
    private val pool: AspectPool,
    private val combiner: AspectCombiner,
) : AspectPalletPort {
    override fun derive(desiredAspect: Aspect): Result<Unit> =
        when {
            knowledge.notDiscoveredResearchMastery() -> missingResearchMastery()
            desiredAspect.isPrimal -> cannotCombinePrimalAspect()
            pool.anyComponentMissingFor(desiredAspect) -> missingComponents()
            else -> combiner.combine(desiredAspect.components[0], desiredAspect.components[1])
        }

    override fun combine(firstAspect: Aspect, secondAspect: Aspect): Result<Unit> =
        when {
            isDrainedOf(firstAspect) || isDrainedOf(secondAspect) -> missingComponents()
            else -> combiner.combine(firstAspect, secondAspect)
        }

    override fun isDrainedOf(aspect: Aspect): Boolean  =
        pool.totalAmountOf(aspect) <= 0

    override fun amountAndBonusOf(aspect: Aspect): Pair<Int, Int> =
        pool.amountOf(aspect) to pool.bonusAmountOf(aspect)

    override fun discoveredAspects(): Array<Aspect> =
        pool.discovered()

}

