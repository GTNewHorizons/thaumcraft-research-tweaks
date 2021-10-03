package elan.tweaks.thaumcraft.research.frontend.domain.model

import elan.tweaks.thaumcraft.research.frontend.domain.failures.DuplicationFailure.Companion.researchNotReady
import elan.tweaks.thaumcraft.research.frontend.domain.failures.MissingResearchFailure.Companion.missingResearchDuplication
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort.Knowledge
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.AspectPool
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.KnowledgeBase
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.ResearchNotes
import elan.tweaks.thaumcraft.research.frontend.domain.ports.required.ScribeTools
import thaumcraft.api.aspects.Aspect
import thaumcraft.common.lib.research.ResearchNoteData


class ResearchProcess constructor(
    private val notes: ResearchNotes,
    private val pool: AspectPool,
    private val knowledgeBase: KnowledgeBase,
    private val scribeTools: ScribeTools
) : ResearchProcessPort {

    override val usedAspectAmounts: Map<Aspect, Int>
        get() = notes.findUsedAspectAmounts()

    override val requiresInkToContinue: Boolean
        get() = scribeTools.areMissingOrEmpty()

    override val data: ResearchNoteData?
        get() = notes.data

    override fun duplicate(): Result<Unit> =
        when {
            knowledgeBase.hasNotDiscovered(Knowledge.ResearchDuplication) -> missingResearchDuplication()
            notReadyToDuplicate() -> researchNotReady()
            else -> notes.duplicate()
        }

    override fun readyToDuplicate(): Boolean =
        notes.present && notes.complete && pool.contains(usedAspectAmounts)

    override fun notEditable(): Boolean =
        missingNotes() || notes.complete

    override fun notesPresent(): Boolean =
        notes.present

    override fun incomplete(): Boolean =
        notes.present && !notes.complete

    override fun shouldObfuscate(aspect: Aspect): Boolean =
        !pool.hasDiscovered(aspect)

    override fun erase(hexKey: String): Result<Unit> =
        notes.erase(hexKey)

    override fun write(hexKey: String, aspect: Aspect): Result<Unit> =
        notes.write(hexKey, aspect)

}
