package elan.tweaks.thaumcraft.research.integration.adapters

import elan.tweaks.thaumcraft.research.domain.ports.required.ResearchNotes
import elan.tweaks.thaumcraft.research.integration.table.container.ResearchTableContainerFactory.RESEARCH_NOTES_SLOT_INDEX
import thaumcraft.common.tiles.TileResearchTable

class ResearchNotesAdapter(
    private val table: TileResearchTable
) : ResearchNotes {
    override val present: Boolean
        get() = notes != null
    
    override val complete: Boolean
        get() = notes.stackTagCompound.getBoolean("complete")

    private val notes get() = 
        table.getStackInSlot(RESEARCH_NOTES_SLOT_INDEX)
}
