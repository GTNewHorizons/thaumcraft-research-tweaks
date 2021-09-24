package elan.tweaks.thaumcraft.research.client.gui

import elan.tweaks.common.gui.Vector
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class VectorTest {

    private val point = Vector(10, 10)

    @Test
    fun `should be greater given any component of other is less then its`() {
        val points = listOf(
            Vector(5, 5),
            
            Vector(5, 10),
            Vector(10, 5),
            
            Vector(5, 20),
            Vector(20, 5),
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
            Vector(15, 15),
            
            Vector(15, 10),
            Vector(10, 15),
        )

        points.forEach { other ->
            assertThat(point < other)
                .withFailMessage { "$point < $other is false" }
                .isTrue
        }
    }

    @Test
    fun `should be equal given both components of other are equal`() {
        val other = Vector(10, 10)
        
        assertThat(point == other).isTrue
    }
}
