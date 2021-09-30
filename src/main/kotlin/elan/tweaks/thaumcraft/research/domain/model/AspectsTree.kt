package elan.tweaks.thaumcraft.research.domain.model

import elan.tweaks.thaumcraft.research.domain.ports.provided.AspectsTreePort
import thaumcraft.api.aspects.Aspect

class AspectsTree : AspectsTreePort {
    override fun areRelated(first: Aspect, second: Aspect): Boolean =
        when {
            first.isPrimal && second.isPrimal -> false
            first.isPrimal -> first in second.getComponentsOrEmpty()
            second.isPrimal -> second in first.getComponentsOrEmpty()
            else -> first in second.getComponentsOrEmpty() || second in first.getComponentsOrEmpty()
        }

    private fun Aspect.getComponentsOrEmpty() = components ?: emptyComponents

    companion object {
        val emptyComponents = arrayOf<Aspect>()
    }
}
