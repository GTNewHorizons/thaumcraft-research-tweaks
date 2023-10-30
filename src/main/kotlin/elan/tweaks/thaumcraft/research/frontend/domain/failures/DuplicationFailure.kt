package elan.tweaks.thaumcraft.research.frontend.domain.failures

class DuplicationFailure(message: String) : Exception(message) {
  companion object {
    fun <ResultT> researchNotReady() =
        Result.failure<ResultT>(DuplicationFailure("research not ready for duplication"))
  }
}
