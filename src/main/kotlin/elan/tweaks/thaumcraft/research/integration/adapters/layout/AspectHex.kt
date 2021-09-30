package elan.tweaks.thaumcraft.research.integration.adapters.layout

import elan.tweaks.common.gui.geometry.VectorXY
import thaumcraft.api.aspects.Aspect

// TODO: probably something similar (without coordinates && UI stuff) must lend in domain
sealed class AspectHex {
    abstract val uiCenterOrigin: VectorXY

    data class Vacant(
        override val uiCenterOrigin: VectorXY,
        val key: String
    ) : AspectHex()

    sealed class Occupied : AspectHex() {
        abstract val aspect: Aspect
        abstract val uiOrigin: VectorXY
        abstract val connectionTargetsCenters: Set<VectorXY>

        data class Root(
            override val uiCenterOrigin: VectorXY,
            override val uiOrigin: VectorXY,
            override val aspect: Aspect,
            override val connectionTargetsCenters: Set<VectorXY>,
        ) : Occupied()

        data class Node(
            override val uiCenterOrigin: VectorXY,
            override val uiOrigin: VectorXY,
            override val aspect: Aspect,
            override val connectionTargetsCenters: Set<VectorXY>,
            val key: String,
            val onRootPath: Boolean,
        ) : Occupied() {
            val notOnRootPath get() = !onRootPath
        }
    }
}
