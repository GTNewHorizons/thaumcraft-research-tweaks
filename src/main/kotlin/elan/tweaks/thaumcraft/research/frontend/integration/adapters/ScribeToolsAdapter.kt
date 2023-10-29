package elan.tweaks.thaumcraft.research.frontend.integration.adapters

import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.ScribeTools
import elan.tweaks.thaumcraft.research.frontend.integration.table.container.ResearchTableContainerFactory.SCRIBE_TOOLS_SLOT_INDEX
import thaumcraft.common.tiles.TileResearchTable

class ScribeToolsAdapter(private val table: TileResearchTable) : ScribeTools {

  override fun areMissingOrEmpty(): Boolean {
    val tools = table.getStackInSlot(SCRIBE_TOOLS_SLOT_INDEX) ?: return true
    return tools.itemDamage == tools.maxDamage
  }
}
