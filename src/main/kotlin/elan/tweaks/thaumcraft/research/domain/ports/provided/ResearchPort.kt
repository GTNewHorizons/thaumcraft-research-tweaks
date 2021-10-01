package elan.tweaks.thaumcraft.research.domain.ports.provided

import thaumcraft.api.aspects.Aspect

interface ResearchPort {
    
    val usedAspectAmounts: Map<Aspect, Int>
    
    fun readyForDuplication(): Boolean = !notReadyForDuplication()
    fun notReadyForDuplication(): Boolean = missingNotes() || incomplete()

    fun missingNotes(): Boolean
    fun incomplete(): Boolean
    fun notEditable(): Boolean
    fun shouldObfuscate(aspect: Aspect): Boolean

    fun erase(hexKey: String): Result<Unit>
    fun write(hexKey: String, aspect: Aspect): Result<Unit>

}
