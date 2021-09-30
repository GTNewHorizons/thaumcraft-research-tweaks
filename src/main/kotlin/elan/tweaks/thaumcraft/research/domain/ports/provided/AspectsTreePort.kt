package elan.tweaks.thaumcraft.research.domain.ports.provided

import thaumcraft.api.aspects.Aspect

interface AspectsTreePort {

    fun areRelated(firstAspect: Aspect, secondAspect: Aspect): Boolean
    
}
