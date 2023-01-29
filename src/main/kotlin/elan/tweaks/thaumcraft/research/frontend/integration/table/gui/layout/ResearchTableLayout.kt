package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout

import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.PlayerInventoryTexture
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ResearchTableInventoryTexture

object ResearchTableLayout {
  val guiScale =
      Scale(
          width = ResearchTableInventoryTexture.scale.width,
          height =
              ResearchTableInventoryTexture.inventoryOrigin.y + PlayerInventoryTexture.scale.height)
}
