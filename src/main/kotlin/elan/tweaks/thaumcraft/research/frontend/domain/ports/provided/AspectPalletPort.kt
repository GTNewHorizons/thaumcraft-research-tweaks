package elan.tweaks.thaumcraft.research.frontend.domain.ports.provided

import thaumcraft.api.aspects.Aspect

interface AspectPalletPort {
  fun derive(desiredAspect: Aspect): Result<Unit>
  fun deriveBatch(desiredAspect: Aspect): Result<Unit>
  fun combine(firstAspect: Aspect, secondAspect: Aspect): Result<Unit>
  fun combineBatch(firstAspect: Aspect, secondAspect: Aspect): Result<Unit>

  fun isDrainedOf(aspect: Aspect): Boolean
  fun amountAndBonusOf(aspect: Aspect): Pair<Int, Int>

  fun missing(aspectAmounts: Map<Aspect, Int>): Boolean
}
