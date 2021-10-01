package elan.tweaks.thaumcraft.research.integration.adapters

import elan.tweaks.thaumcraft.research.domain.ports.required.ScribeTools
import elan.tweaks.thaumcraft.research.integration.table.container.ResearchTableContainerFactory.SCRIBE_TOOLS_SLOT_INDEX
import thaumcraft.common.tiles.TileResearchTable

class ScribeToolsAdapter(
    private val table: TileResearchTable
) : ScribeTools {

    override fun getNotEmptyAndPresent(): Boolean {
        val tools = table.getStackInSlot(SCRIBE_TOOLS_SLOT_INDEX) ?: return false
        return tools.itemDamage != tools.maxDamage
    } 

}
