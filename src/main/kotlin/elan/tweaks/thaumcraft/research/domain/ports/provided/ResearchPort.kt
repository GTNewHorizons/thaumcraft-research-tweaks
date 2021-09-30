package elan.tweaks.thaumcraft.research.domain.ports.provided

import thaumcraft.api.aspects.Aspect

interface ResearchPort {
    
    fun missingNotes(): Boolean
    fun incomplete(): Boolean
    fun notEditable(): Boolean
    fun shouldObfuscate(aspect: Aspect): Boolean
    
    fun erase(hexKey: String): Result<Unit>
    fun write(hexKey: String, aspect: Aspect): Result<Unit>

}
