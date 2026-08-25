package elan.tweaks.thaumcraft.research.frontend.domain.model

import elan.tweaks.common.ext.ResultExt.success
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectPalletPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort.Knowledge
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.AspectCombiner
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.AspectPool
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.KnowledgeBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import thaumcraft.api.aspects.Aspect

internal class AspectPalletTest {

  // The public Aspect constructor registers each instance into the JVM-static Aspect.aspects
  // registry and throws on duplicate tags. Every test uses its own unique tags as a fail-fast
  // guard, and tearDown() unregisters them after each test.
  private val createdAspects = mutableListOf<Aspect>()

  @AfterEach
  fun tearDown() = createdAspects.forEach { Aspect.aspects.remove(it.tag) }

  private fun primal(tag: String) = Aspect(tag, 0xFFFFFF, null).also(createdAspects::add)
  private fun compound(tag: String, vararg components: Aspect) =
      Aspect(tag, 0xFFFFFF, components).also(createdAspects::add)

  @Test
  fun `single combine sends exactly one packet when ingredients are available`() {
    val air = primal("aspect-pallet-test-1-air")
    val fire = primal("aspect-pallet-test-1-fire")
    val pallet =
        palletOf(
            totals = mapOf(air to 3, fire to 5), batchSize = 5)

    val result = pallet.combine(air, fire)

    assertThat(result.isSuccess).isTrue
    assertThat(pallet.combineCallCount).isEqualTo(1)
  }

  @Test
  fun `batch combine does not send more packets than affordable ingredients`() {
    val air = primal("aspect-pallet-test-2-air")
    val fire = primal("aspect-pallet-test-2-fire")
    val pallet =
        palletOf(
            totals = mapOf(air to 3, fire to 1),
            batchSize = 5,
            expert = true)

    val result = pallet.combineBatch(air, fire)

    assertThat(result.isSuccess).isTrue
    assertThat(pallet.combineCallCount).isEqualTo(1)
  }

  @Test
  fun `batch combine consumes the full batch when ingredients allow it`() {
    val air = primal("aspect-pallet-test-3-air")
    val fire = primal("aspect-pallet-test-3-fire")
    val pallet =
        palletOf(
            totals = mapOf(air to 12, fire to 8),
            batchSize = 5,
            expert = true)

    val result = pallet.combineBatch(air, fire)

    assertThat(result.isSuccess).isTrue
    assertThat(pallet.combineCallCount).isEqualTo(5)
  }

  @Test
  fun `batch combine fails without sending packets when ingredients are missing`() {
    val air = primal("aspect-pallet-test-4-air")
    val fire = primal("aspect-pallet-test-4-fire")
    val pallet =
        palletOf(
            totals = mapOf(air to 3, fire to 0),
            batchSize = 5,
            expert = true)

    val result = pallet.combineBatch(air, fire)

    assertThat(result.isFailure).isTrue
    assertThat(pallet.combineCallCount).isEqualTo(0)
  }

  @Test
  fun `batch combine without expertise sends a single packet`() {
    val air = primal("aspect-pallet-test-5-air")
    val fire = primal("aspect-pallet-test-5-fire")
    val pallet =
        palletOf(
            totals = mapOf(air to 12, fire to 8),
            batchSize = 5,
            expert = false)

    val result = pallet.combineBatch(air, fire)

    assertThat(result.isSuccess).isTrue
    assertThat(pallet.combineCallCount).isEqualTo(1)
  }

  @Test
  fun `batch derive sends only as many packets as the weaker ingredient allows`() {
    val air = primal("aspect-pallet-test-6-air")
    val fire = primal("aspect-pallet-test-6-fire")
    val combo = compound("aspect-pallet-test-6-combo", air, fire)
    val pallet =
        palletOf(
            totals = mapOf(air to 5, fire to 2),
            batchSize = 5,
            master = true)

    val result = pallet.deriveBatch(combo)

    assertThat(result.isSuccess).isTrue
    assertThat(pallet.combineCallCount).isEqualTo(2)
  }

  @Test
  fun `derive fails without sending packets when one ingredient is missing`() {
    val air = primal("aspect-pallet-test-7-air")
    val fire = primal("aspect-pallet-test-7-fire")
    val combo = compound("aspect-pallet-test-7-combo", air, fire)
    val pallet =
        palletOf(
            totals = mapOf(air to 5, fire to 0),
            batchSize = 5,
            master = true)

    val result = pallet.derive(combo)

    assertThat(result.isFailure).isTrue
    assertThat(pallet.combineCallCount).isEqualTo(0)
  }

  @Test
  fun `batch combine propagates a combiner failure and stops sending packets`() {
    val air = primal("aspect-pallet-test-9-air")
    val fire = primal("aspect-pallet-test-9-fire")
    val failure = IllegalStateException("combiner exploded")
    val combiner = FakeAspectCombiner(failOnCall = 2, failure = failure)
    val pallet =
        TestPallet(
            port =
                AspectPallet(
                    base = FakeKnowledgeBase(setOf(Knowledge.ResearchExpertise)),
                    pool = FakeAspectPool(mapOf(air to 5, fire to 5)),
                    combiner = combiner,
                    batchSize = 5),
            combiner = combiner)

    val result = pallet.combineBatch(air, fire)

    assertThat(result.isFailure).isTrue
    assertThat(result.exceptionOrNull()).isEqualTo(failure)
    assertThat(pallet.combineCallCount).isEqualTo(2)
  }

  @Test
  fun `derive fails when research mastery is not discovered`() {
    val air = primal("aspect-pallet-test-8-air")
    val fire = primal("aspect-pallet-test-8-fire")
    val combo = compound("aspect-pallet-test-8-combo", air, fire)
    val pallet =
        palletOf(
            totals = mapOf(air to 5, fire to 5),
            batchSize = 5,
            master = false)

    val result = pallet.derive(combo)

    assertThat(result.isFailure).isTrue
    assertThat(pallet.combineCallCount).isEqualTo(0)
  }

  private fun palletOf(
      totals: Map<Aspect, Int>,
      batchSize: Int,
      master: Boolean = true,
      expert: Boolean = true,
  ): TestPallet {
    val pool = FakeAspectPool(totals)
    val combiner = FakeAspectCombiner()
    val discovered = buildSet {
      if (master) add(Knowledge.ResearchMastery)
      if (expert) add(Knowledge.ResearchExpertise)
    }
    val pallet =
        AspectPallet(
            base = FakeKnowledgeBase(discovered),
            pool = pool,
            combiner = combiner,
            batchSize = batchSize)
    return TestPallet(pallet, combiner)
  }

  private class TestPallet(
      val port: AspectPalletPort,
      private val combiner: FakeAspectCombiner,
  ) : AspectPalletPort by port {
    val combineCallCount: Int get() = combiner.callCount
  }

  private class FakeAspectPool(initialTotals: Map<Aspect, Int>) : AspectPool {
    private val totals = initialTotals.toMutableMap()

    override fun hasDiscovered(aspect: Aspect): Boolean = false

    override fun allDiscovered(): Array<Aspect> = emptyArray()

    override fun amountOf(aspect: Aspect): Int = totals[aspect] ?: 0

    override fun bonusAmountOf(aspect: Aspect): Int = 0

    override fun totalAmountOf(aspect: Aspect): Int = totals[aspect] ?: 0

    override fun contains(aspectAmounts: Map<Aspect, Int>): Boolean =
        aspectAmounts.all { (aspect, amount) -> totalAmountOf(aspect) >= amount }
  }

  private class FakeKnowledgeBase(discovered: Set<Knowledge>) : KnowledgeBase {
    private val discovered = discovered.toSet()

    override fun hasDiscovered(knowledge: Knowledge): Boolean = knowledge in discovered
  }

  private class FakeAspectCombiner(
      private val failOnCall: Int? = null,
      private val failure: Throwable = IllegalStateException("failed combine"),
  ) : AspectCombiner {
    var callCount: Int = 0
      private set

    override fun combine(firstAspect: Aspect, secondAspect: Aspect): Result<Unit> {
      callCount++
      if (callCount == failOnCall) return Result.failure(failure)
      return success()
    }
  }
}
