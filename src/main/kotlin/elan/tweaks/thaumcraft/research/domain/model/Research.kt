package elan.tweaks.thaumcraft.research.domain.model

import elan.tweaks.common.gui.geometry.VectorXY
import elan.tweaks.thaumcraft.research.domain.ports.provided.ResearchPort
import elan.tweaks.thaumcraft.research.domain.ports.required.ResearchNotes
import thaumcraft.api.aspects.Aspect


class Research(
    private val notes: ResearchNotes
) : ResearchPort {

    override fun missingOrComplete(): Boolean =
        !notes.present || notes.complete
    
    sealed class Hex {
        abstract val uiOrigin: VectorXY

        data class Vacant(override val uiOrigin: VectorXY) : Hex()
        sealed class Occupied : Hex() {
            abstract val aspect: Aspect

            data class Root(
                override val uiOrigin: VectorXY,
                override val aspect: Aspect
            ) : Occupied()
            
            data class Node(
                override val uiOrigin: VectorXY,
                override val aspect: Aspect
            ) : Occupied()
        }
    }

}
