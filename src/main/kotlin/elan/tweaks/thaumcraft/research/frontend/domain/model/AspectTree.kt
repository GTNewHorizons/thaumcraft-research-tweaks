package elan.tweaks.thaumcraft.research.frontend.domain.model

import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectsTreePort
import kotlin.math.absoluteValue
import thaumcraft.api.aspects.Aspect

object AspectTree : AspectsTreePort {

  private val orderedBalance = findBalance()
  override fun toString(): String = "AspectTree: $orderedBalance"

  private fun findBalance(): OrderedBalance {
    val (aspectToOrder, affinityToOrder, affinityToEntropy) = findOrderAndAffinities()
    val (definiteOrder, definiteEntropy) = balance(affinityToOrder, affinityToEntropy)

    return OrderedBalance(
        aspectToOrder = aspectToOrder,
        orderLeaning = definiteOrder.sortedBy(aspectToOrder::getValue),
        entropyLeaning = definiteEntropy.sortedBy(aspectToOrder::getValue))
  }

  private fun findOrderAndAffinities():
      Triple<MutableMap<Aspect, Int>, MutableMap<Aspect, Float>, MutableMap<Aspect, Float>> {
    val descendants = mutableMapOf<Aspect, MutableSet<Aspect>>()
    Aspect.getCompoundAspects().forEach { aspect ->
      descendants.getOrPut(aspect.components[0], ::mutableSetOf) += aspect
      descendants.getOrPut(aspect.components[1], ::mutableSetOf) += aspect
    }

    val aspectToOrder =
        mutableMapOf<Aspect, Int>(
            Aspect.ORDER to 10,
            Aspect.EARTH to 12,
            Aspect.WATER to 14,
            Aspect.ENTROPY to 11,
            Aspect.FIRE to 13,
            Aspect.AIR to 15,
        )

    val affinityToOrder =
        mutableMapOf(
            Aspect.ORDER to 1f,
            Aspect.EARTH to 1f,
            Aspect.WATER to 1f,
            Aspect.ENTROPY to 0f,
            Aspect.FIRE to 0f,
            Aspect.AIR to 0f,
        )

    val affinityToEntropy =
        mutableMapOf(
            Aspect.ORDER to 0f,
            Aspect.EARTH to 0f,
            Aspect.WATER to 0f,
            Aspect.ENTROPY to 1f,
            Aspect.FIRE to 1f,
            Aspect.AIR to 1f,
        )

    var unorderedAspects = Aspect.getPrimalAspects().flatMap(descendants::getValue).toSet()
    while (unorderedAspects.isNotEmpty()) {
      val (orderDerivable, orderNotYetDerivable) =
          unorderedAspects.partition {
            it.components[0] in aspectToOrder && it.components[1] in aspectToOrder
          }

      aspectToOrder +=
          orderDerivable.associateWith { aspect ->
            aspectToOrder.getValue(aspect.components[0]) +
                aspectToOrder.getValue(aspect.components[1])
          }

      affinityToOrder +=
          orderDerivable.associateWith { aspect ->
            (affinityToOrder.getValue(aspect.components[0]) +
                affinityToOrder.getValue(aspect.components[1])) / 2
          }
      affinityToEntropy +=
          orderDerivable.associateWith { aspect ->
            (affinityToEntropy.getValue(aspect.components[0]) +
                affinityToEntropy.getValue(aspect.components[1])) / 2
          }
      unorderedAspects =
          orderNotYetDerivable.toSet() +
              orderDerivable
                  .flatMap { descendants[it] ?: emptySet() }
                  .filterNot { it in aspectToOrder }
    }
    return Triple(aspectToOrder, affinityToOrder, affinityToEntropy)
  }

  private fun balance(
      affinityToOrder: MutableMap<Aspect, Float>,
      affinityToEntropy: MutableMap<Aspect, Float>
  ): Pair<MutableSet<Aspect>, MutableSet<Aspect>> {
    val definiteOrder =
        affinityToOrder
            .filter { (key, orderAffinity) -> orderAffinity > affinityToEntropy.getValue(key) }
            .keys
            .toMutableSet()
    val definiteEntropy =
        affinityToEntropy
            .filter { (key, entropyAffinity) -> entropyAffinity > affinityToOrder.getValue(key) }
            .keys
            .toMutableSet()
    val balanced =
        affinityToOrder
            .filter { (key, orderAffinity) -> orderAffinity == affinityToEntropy.getValue(key) }
            .keys
            .toMutableSet()

    val delta = definiteOrder.size - definiteEntropy.size

    when {
      delta > 0 -> {
        val additionToEntropy = balanced.take(delta)
        definiteEntropy += additionToEntropy
        balanced -= additionToEntropy
      }
      delta < 0 -> {
        val additionToOrder = balanced.take(delta.absoluteValue)
        definiteOrder += additionToOrder
        balanced -= additionToOrder
      }
    }

    definiteOrder += balanced.toList().take(balanced.size / 2)
    definiteEntropy += balanced.toList().drop(balanced.size / 2)
    return Pair(definiteOrder, definiteEntropy)
  }

  override fun orderOf(aspect: Aspect): Int = orderedBalance.aspectToOrder[aspect] ?: Int.MAX_VALUE
  override fun allOrderLeaning(): List<Aspect> = orderedBalance.orderLeaning
  override fun allEntropyLeaning(): List<Aspect> = orderedBalance.entropyLeaning

  override fun areRelated(first: Aspect, second: Aspect): Boolean =
      when {
        first.isPrimal && second.isPrimal -> false
        first.isPrimal -> first in second.getComponentsOrEmpty()
        second.isPrimal -> second in first.getComponentsOrEmpty()
        else -> first in second.getComponentsOrEmpty() || second in first.getComponentsOrEmpty()
      }

  private fun Aspect.getComponentsOrEmpty() = components ?: emptyComponents

  private val emptyComponents = emptyArray<Aspect>()

  data class OrderedBalance(
      val aspectToOrder: Map<Aspect, Int>,
      val orderLeaning: List<Aspect>,
      val entropyLeaning: List<Aspect>
  )
}
