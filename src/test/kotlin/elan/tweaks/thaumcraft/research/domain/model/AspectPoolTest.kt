package elan.tweaks.thaumcraft.research.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AspectPoolTest {

    @Test
    fun `sort aspects ascending according to ordinal when created`() {
        val tagToOrdinal = listOf(
            "Air", "Water", "Earth", "Fire", "Order", "Entropy",
            "Void", "Light", "Motion", "Life",
            "Beast"
        ).mapIndexed { index: Int, tag: String -> tag to index.toUInt() }.toMap()
        val aspectPoints = setOf(
            AspectPoints("Beast", 5u),
            AspectPoints("Order", 1u),
            AspectPoints("Light", 1u),
            AspectPoints("Motion", 3u),
            AspectPoints("Air", 10u),
        )
        val expectedAspectPointOrder = listOf(
            AspectPoints("Air", 10u), AspectPoints("Order", 1u),
            AspectPoints("Light", 1u), AspectPoints("Motion", 3u),
            AspectPoints("Beast", 5u),
        )

        val pool = AspectPool(tagToOrdinal, aspectPoints)

        assertThat(pool.aspectsPoints).containsExactlyElementsOf(expectedAspectPointOrder)
    }

}
