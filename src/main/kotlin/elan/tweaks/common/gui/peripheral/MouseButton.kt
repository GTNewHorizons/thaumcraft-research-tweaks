package elan.tweaks.common.gui.peripheral

import org.lwjgl.input.Mouse

sealed class MouseButton(private val index: Int) {
  object Left : MouseButton(0)
  object Right : MouseButton(1)
  object Middle : MouseButton(2)
  class Unknown(index: Int) : MouseButton(index)

  fun isDown() = Mouse.isButtonDown(index)

  companion object {
    fun of(index: Int): MouseButton =
        when (index) {
          0 -> Left
          1 -> Right
          2 -> Middle
          else -> Unknown(index)
        }
  }
}
