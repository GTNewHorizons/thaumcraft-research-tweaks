package elan.tweaks.thaumcraft.research.domain.ports.required

import thaumcraft.api.aspects.Aspect

interface AspectPool {
    fun hasDiscovered(aspect: Aspect): Boolean
    fun allDiscovered(): Array<Aspect>
    
    fun amountOf(aspect: Aspect): Int
    fun bonusAmountOf(aspect: Aspect): Int
    fun totalAmountOf(aspect: Aspect): Int

    fun anyComponentMissingFor(aspect: Aspect): Boolean
    fun missing(usedAspectAmounts: Map<Aspect, Int>): Boolean
}
