package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.dto

import elan.tweaks.common.gui.dto.VectorXY
import thaumcraft.api.aspects.Aspect

// TODO: consider something similar (without coordinates && UI stuff) for domain in case of backend
// part rewrite
sealed class AspectHex {
  abstract val uiOrigin: VectorXY

  data class Vacant(override val uiOrigin: VectorXY, val key: String) : AspectHex()

  sealed class Occupied : AspectHex() {
    abstract val aspect: Aspect
    abstract val uiCenter: VectorXY
    abstract val connectionTargetsCenters: Set<VectorXY>

    data class Root(
        override val uiOrigin: VectorXY,
        override val uiCenter: VectorXY,
        override val aspect: Aspect,
        override val connectionTargetsCenters: Set<VectorXY>,
    ) : Occupied()

    data class Node(
        override val uiOrigin: VectorXY,
        override val uiCenter: VectorXY,
        override val aspect: Aspect,
        override val connectionTargetsCenters: Set<VectorXY>,
        val key: String,
        val onRootPath: Boolean,
    ) : Occupied() {
      val notOnRootPath
        get() = !onRootPath
    }
  }
}
