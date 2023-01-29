package elan.tweaks.thaumcraft.research.frontend.domain.failures

class AspectCombinationFailure(message: String) : Exception(message) {
  companion object {
    fun <ResultT> cannotDerivePrimalAspect() =
        Result.failure<ResultT>(AspectCombinationFailure("cannot derive primal aspect"))

    fun <ResultT> missingComponents() =
        Result.failure<ResultT>(AspectCombinationFailure("missing components"))
  }
}
