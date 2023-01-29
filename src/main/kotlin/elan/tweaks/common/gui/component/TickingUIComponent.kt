package elan.tweaks.common.gui.component

interface TickingUIComponent : UIComponent {
  fun onTick(partialTicks: Float, context: UIContext)
}
