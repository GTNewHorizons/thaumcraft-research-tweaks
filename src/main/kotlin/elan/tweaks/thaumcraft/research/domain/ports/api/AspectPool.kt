package elan.tweaks.thaumcraft.research.domain.ports.api

import elan.tweaks.thaumcraft.research.domain.model.AspectPoints

interface AspectPool {
    fun hasAvailable(aspectTag: String): Boolean
    fun takeBack(aspectTag: String): Result<Unit>
    fun use(vararg aspectPoints: AspectPoints): Result<Unit>
}
