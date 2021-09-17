package elan.tweaks.thaumcraft.research.domain.model

import elan.tweaks.thaumcraft.research.domain.ports.api.AspectPool

data class AspectPoolImpl(
    private val aspects: MutableList<AspectPoints> 
    // We probably would want to have sorting somewhere around here?
    // This would require richer aspect dependency info
): AspectPool {
    override fun hasAvailable(aspectTag: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun takeBack(aspectTag: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun use(vararg aspectPoints: AspectPoints): Result<Unit> {
        TODO("Not yet implemented")
    }
}

data class AspectPoints(
    val tag: String,
    val amount: UInt
)

interface AspectCombinationController {
    
}
