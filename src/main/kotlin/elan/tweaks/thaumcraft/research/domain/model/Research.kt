package elan.tweaks.thaumcraft.research.domain.model

import elan.tweaks.thaumcraft.research.domain.ports.provided.ResearchPort
import elan.tweaks.thaumcraft.research.domain.ports.required.ResearchNotes


class Research(
    private val notes: ResearchNotes
) : ResearchPort {
    
    override fun missingNotes(): Boolean =
        !notes.present

}

