package elan.tweaks.common.gui.drawing

import elan.tweaks.common.gui.geometry.Vector3D
import net.minecraft.client.renderer.RenderHelper
import thaumcraft.api.aspects.Aspect
import thaumcraft.client.lib.UtilsFX

object AspectDrawer {
    fun drawTag(screenOrigin: Vector3D, aspect: Aspect?, amount: Float = 0f, bonus: Int = 0, blend: Int = 771, alpha: Float = 1f) {
        UtilsFX.drawTag(screenOrigin.x, screenOrigin.y, aspect, amount, bonus, screenOrigin.z, blend, alpha, false)
        RenderHelper.disableStandardItemLighting()
    }

    fun drawMonochromeTag(screenOrigin: Vector3D, aspect: Aspect?, alpha: Float) {
        UtilsFX.drawTag(screenOrigin.x.toDouble(), screenOrigin.y.toDouble(), aspect, ZERO_AMOUNT, ZERO_BONUS, screenOrigin.z, BLEND, alpha, true)
        RenderHelper.disableStandardItemLighting()
    }

    private const val BLEND = 771
    private const val ZERO_BONUS = 0
    private const val ZERO_AMOUNT = 0f
}
