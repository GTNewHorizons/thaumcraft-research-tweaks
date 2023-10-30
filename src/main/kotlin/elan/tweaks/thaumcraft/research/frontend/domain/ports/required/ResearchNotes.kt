package elan.tweaks.thaumcraft.research.frontend.domain.ports.required

import thaumcraft.api.aspects.Aspect
import thaumcraft.common.lib.research.ResearchNoteData

interface ResearchNotes {
  val present: Boolean
  val valid: Boolean

  val complete: Boolean

  val data: ResearchNoteData?

  fun erase(hexKey: String): Result<Unit>
  fun write(hexKey: String, aspect: Aspect): Result<Unit>

  fun duplicate(): Result<Unit>

  fun findUsedAspectAmounts(): Map<Aspect, Int>
}
