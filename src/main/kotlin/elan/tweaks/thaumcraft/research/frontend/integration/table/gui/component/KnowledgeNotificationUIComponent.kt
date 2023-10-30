package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.component

import elan.tweaks.common.gui.component.ForegroundUIComponent
import elan.tweaks.common.gui.component.UIContext
import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.VectorXY
import org.lwjgl.opengl.GL11
import thaumcraft.client.lib.PlayerNotifications
import thaumcraft.common.Thaumcraft

class KnowledgeNotificationUIComponent : ForegroundUIComponent {
  override fun onDrawForeground(uiMousePosition: VectorXY, scale: Scale, context: UIContext) {
    val time = System.nanoTime() / 1000000L
    if (PlayerNotifications.getListAndUpdate(time).size > 0) {
      GL11.glPushMatrix()
      Thaumcraft.instance.renderEventHandler.notifyHandler.renderNotifyHUD(
          scale.width.toDouble(), scale.height.toDouble(), time)
      GL11.glPopMatrix()
    }
  }
}
