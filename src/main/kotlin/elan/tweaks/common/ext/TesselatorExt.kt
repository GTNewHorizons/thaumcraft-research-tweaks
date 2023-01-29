package elan.tweaks.common.ext

import net.minecraft.client.renderer.Tessellator

fun Tessellator.drawQuads(configureTesselation: Tessellator.() -> Unit) {
  startDrawingQuads()
  configureTesselation()
  draw()
}

fun Tessellator.draw(drawMode: Int, configureTesselation: Tessellator.() -> Unit) {
  startDrawing(drawMode)
  configureTesselation()
  draw()
}
