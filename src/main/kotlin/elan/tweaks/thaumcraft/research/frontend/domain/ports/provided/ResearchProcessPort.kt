package elan.tweaks.thaumcraft.research.frontend.domain.ports.provided

import thaumcraft.api.aspects.Aspect
import thaumcraft.common.lib.research.ResearchNoteData

interface ResearchProcessPort {

  val usedAspectAmounts: Map<Aspect, Int>
  val requiresInkToContinue: Boolean
  val data: ResearchNoteData?

  fun notReadyToDuplicate(): Boolean = !readyToDuplicate()
  fun readyToDuplicate(): Boolean
  fun duplicate(): Result<Unit>

  fun missingNotes(): Boolean = !notesPresent()
  fun notesPresent(): Boolean
  fun notesCorrupted(): Boolean
  fun incomplete(): Boolean = !complete()
  fun complete(): Boolean
  fun notEditable(): Boolean
  fun shouldObfuscate(aspect: Aspect): Boolean

  fun erase(hexKey: String): Result<Unit>
  fun write(hexKey: String, aspect: Aspect): Result<Unit>
}
