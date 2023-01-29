package elan.tweaks.thaumcraft.research.frontend.integration.failures

class HexNotFoundFailure(message: String) : Exception(message) {
  companion object {
    fun <ResultT> screenHexNotFound(hexKey: String) =
        Result.failure<ResultT>(HexNotFoundFailure("screen hex for $hexKey not found"))
  }
}
