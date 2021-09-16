package elan.tweaks.thaumcraft.research.client.gui

import elan.tweaks.thaumcraft.research.integration.client.gui.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PointTest {

    private val point = Point(10u, 10u)

    @Test
    fun `should be greater given any component of other is less then its`() {
        val points = listOf(
            Point(5u, 5u),
            
            Point(5u, 10u),
            Point(10u, 5u),
            
            Point(5u, 20u),
            Point(20u, 5u),
        )

        points.forEach { other ->
            assertThat(point > other)
                .withFailMessage { "$point > $other is false" }
                .isTrue
        }
    }

    @Test
    fun `should be less given both components of other are greater or equal to its`() {
        val points = listOf(
            Point(15u, 15u),
            
            Point(15u, 10u),
            Point(10u, 15u),
        )

        points.forEach { other ->
            assertThat(point < other)
                .withFailMessage { "$point < $other is false" }
                .isTrue
        }
    }

    @Test
    fun `should be equal given both components of other are equal`() {
        val other = Point(10u, 10u)
        
        assertThat(point == other).isTrue
    }
}
