package elan.tweaks.thaumcraft.research.domain.ports.provided

import thaumcraft.api.aspects.Aspect

interface ResearchPort {
    
    fun missingNotes(): Boolean
    fun incomplete(): Boolean
    fun shouldObfuscate(aspect: Aspect): Boolean
    
}
