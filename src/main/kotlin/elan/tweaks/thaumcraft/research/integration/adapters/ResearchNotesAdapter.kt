package elan.tweaks.thaumcraft.research.integration.adapters

import elan.tweaks.thaumcraft.research.domain.ports.required.ResearchNotes
import elan.tweaks.thaumcraft.research.integration.table.container.ResearchTableContainerFactory.RESEARCH_NOTES_SLOT_INDEX
import thaumcraft.common.tiles.TileResearchTable

class ResearchNotesAdapter(
    private val table: TileResearchTable
) : ResearchNotes {
    override val present: Boolean
        get() = table.getStackInSlot(RESEARCH_NOTES_SLOT_INDEX) != null
}
