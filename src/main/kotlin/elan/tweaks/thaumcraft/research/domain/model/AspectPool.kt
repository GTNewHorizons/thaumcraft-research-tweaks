package elan.tweaks.thaumcraft.research.domain.model

import com.google.common.base.Preconditions


class AspectPool(
    private val tagToOrdinal: Map<String, UInt>,
    aspectsPoints: Set<AspectPoints>
) {
    
    val aspectsPoints: List<AspectPoints> = aspectsPoints.sortedBy { tagToOrdinal[it.tag]!! }


    companion object {
        // do not create if ordinal not contains all available aspects
    }
}

data class AspectPoints(
    val tag: String,
    val amount: UInt
)


// Unused for now
interface AspectCombinerController {
    fun select(aspectTag: String): Result<Unit>
    fun combine(): Result<Unit>
    fun deselectLeft(): Result<Unit>
    fun deselectRight(): Result<Unit>
}

interface AspectCombinerView {

}

interface NotificationLog {
    fun notifyGain(points: AspectPoints)
}
