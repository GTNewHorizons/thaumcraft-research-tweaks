package elan.tweaks.thaumcraft.research.frontend.domain.model

import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.AspectPool
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.ResearchNotes
import thaumcraft.api.aspects.Aspect


class Research(
    private val notes: ResearchNotes,
    private val pool: AspectPool,
) : ResearchPort {

    override val usedAspectAmounts: Map<Aspect, Int>
        get() = notes.findUsedAspectAmounts()

    override fun notEditable(): Boolean =
        missingNotes() || notes.complete

    override fun missingNotes(): Boolean =
        !notes.present

    override fun incomplete(): Boolean =
        notes.present && !notes.complete
        
    override fun shouldObfuscate(aspect: Aspect): Boolean =
        !pool.hasDiscovered(aspect)

    override fun erase(hexKey: String): Result<Unit> =
        notes.erase(hexKey)

    override fun write(hexKey: String, aspect: Aspect): Result<Unit> =
        notes.write(hexKey, aspect)

}
