package elan.tweaks.thaumcraft.research.client.gui

import elan.tweaks.common.gui.dto.Vector2D
import elan.tweaks.common.gui.dto.VectorXY
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class VectorXYTest {

  private val point: VectorXY = Vector2D(10, 10)

  @Test
  fun `should be greater given any component of other is less then its`() {
    val points =
        listOf(
            Vector2D(5, 5),
            Vector2D(5, 10),
            Vector2D(10, 5),
            Vector2D(5, 20),
            Vector2D(20, 5),
        )

    points.forEach { other ->
      assertThat(point > other).withFailMessage { "$point > $other is false" }.isTrue
    }
  }

  @Test
  fun `should be less given both components of other are greater or equal to its`() {
    val points =
        listOf(
            Vector2D(15, 15),
            Vector2D(15, 10),
            Vector2D(10, 15),
        )

    points.forEach { other ->
      assertThat(point < other).withFailMessage { "$point < $other is false" }.isTrue
    }
  }

  @Test
  fun `should be equal given both components of other are equal`() {
    val other = Vector2D(10, 10)

    assertThat(point == other).isTrue
  }
}
