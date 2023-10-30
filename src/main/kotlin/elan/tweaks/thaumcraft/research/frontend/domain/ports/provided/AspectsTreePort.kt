package elan.tweaks.thaumcraft.research.frontend.domain.ports.provided

import thaumcraft.api.aspects.Aspect

interface AspectsTreePort {

  fun areRelated(first: Aspect, second: Aspect): Boolean
  fun orderOf(aspect: Aspect): Int
  fun allEntropyLeaning(): List<Aspect>
  fun allOrderLeaning(): List<Aspect>
}
