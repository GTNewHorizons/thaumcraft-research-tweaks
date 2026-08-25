package elan.tweaks.thaumcraft.research.frontend.domain.model

import elan.tweaks.common.ext.ResultExt.success
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

  override fun deriveBatch(desiredAspect: Aspect): Result<Unit> = derive(desiredAspect, batchSize)

  override fun derive(desiredAspect: Aspect): Result<Unit> = derive(desiredAspect, 1)

  private fun derive(desiredAspect: Aspect, count: Int): Result<Unit> =
      when {
        base.hasNotDiscovered(Knowledge.ResearchMastery) -> missingResearchMastery()
        desiredAspect.isPrimal -> cannotDerivePrimalAspect()
        else -> combineBatch(desiredAspect.components[0], desiredAspect.components[1], count)
      }

  override fun combineBatch(firstAspect: Aspect, secondAspect: Aspect): Result<Unit> =
      combineBatch(
          firstAspect,
          secondAspect,
          if (base.hasDiscovered(Knowledge.ResearchExpertise)) batchSize else 1)

  override fun combine(firstAspect: Aspect, secondAspect: Aspect): Result<Unit> =
      combineBatch(firstAspect, secondAspect, 1)

  private fun combineBatch(firstAspect: Aspect, secondAspect: Aspect, count: Int): Result<Unit> {
    val maxAffordable = minOf(pool.totalAmountOf(firstAspect), pool.totalAmountOf(secondAspect))
    val toCombine = count.coerceAtMost(maxAffordable)
    if (toCombine <= 0) return missingComponents()
    repeat(toCombine) {
      val result = combiner.combine(firstAspect, secondAspect)
      if (result.isFailure) return result
    }
    return success()
  }

  override fun isDrainedOf(aspect: Aspect): Boolean = pool.totalAmountOf(aspect) <= 0
}
