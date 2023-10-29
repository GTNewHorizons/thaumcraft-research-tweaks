package elan.tweaks.thaumcraft.research.frontend.domain.model

import elan.tweaks.thaumcraft.research.frontend.domain.failures.AspectCombinationFailure.Companion.cannotDerivePrimalAspect
import elan.tweaks.thaumcraft.research.frontend.domain.failures.AspectCombinationFailure.Companion.missingComponents
import elan.tweaks.thaumcraft.research.frontend.domain.failures.MissingResearchFailure.Companion.missingResearchMastery
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectPalletPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort.Knowledge
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.AspectCombiner
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.AspectPool
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.KnowledgeBase
import thaumcraft.api.aspects.Aspect

class AspectPallet
constructor(
    private val base: KnowledgeBase,
    private val pool: AspectPool,
    private val combiner: AspectCombiner,
    private val batchSize: Int
) : AspectPalletPort {

  override fun amountAndBonusOf(aspect: Aspect): Pair<Int, Int> =
      pool.amountOf(aspect) to pool.bonusAmountOf(aspect)

  override fun missing(aspectAmounts: Map<Aspect, Int>): Boolean = pool.missing(aspectAmounts)

  override fun deriveBatch(desiredAspect: Aspect): Result<Unit> = batch { derive(desiredAspect) }

  override fun derive(desiredAspect: Aspect): Result<Unit> =
      when {
        base.hasNotDiscovered(Knowledge.ResearchMastery) -> missingResearchMastery()
        desiredAspect.isPrimal -> cannotDerivePrimalAspect()
        pool.anyComponentMissingFor(desiredAspect) -> missingComponents()
        else -> combiner.combine(desiredAspect.components[0], desiredAspect.components[1])
      }

  override fun combineBatch(firstAspect: Aspect, secondAspect: Aspect): Result<Unit> =
      if (base.hasDiscovered(Knowledge.ResearchExpertise))
          batch { combine(firstAspect, secondAspect) }
      else combine(firstAspect, secondAspect)

  override fun combine(firstAspect: Aspect, secondAspect: Aspect): Result<Unit> =
      when {
        isDrainedOf(firstAspect) || isDrainedOf(secondAspect) -> missingComponents()
        else -> combiner.combine(firstAspect, secondAspect)
      }

  override fun isDrainedOf(aspect: Aspect): Boolean = pool.totalAmountOf(aspect) <= 0

  private fun <ResultT> batch(function: () -> Result<ResultT>): Result<ResultT> {
    val batchResults = (1..batchSize).map { function() }

    return batchResults.firstOrNull { it.isSuccess } ?: batchResults.first()
  }
}
