package elan.tweaks.thaumcraft.research.integration.adapters.layout

import elan.tweaks.common.gui.geometry.VectorXY
import thaumcraft.api.aspects.Aspect

sealed class AspectHex {
    abstract val uiOrigin: VectorXY

    data class Vacant(override val uiOrigin: VectorXY) : AspectHex()
    sealed class Occupied : AspectHex() {
        abstract val aspect: Aspect
        abstract val uiCenter: VectorXY
        abstract val connectionTargetsCenters: Set<VectorXY>

        data class Root(
            override val uiOrigin: VectorXY,
            override val uiCenter: VectorXY,
            override val aspect: Aspect,
            override val connectionTargetsCenters: Set<VectorXY>
        ) : Occupied()

        data class Node(
            override val uiOrigin: VectorXY,
            override val uiCenter: VectorXY,
            override val aspect: Aspect,
            val disconnectedFromRoot: Boolean,
            override val connectionTargetsCenters: Set<VectorXY>
        ) : Occupied()
    }
}
