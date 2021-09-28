package elan.tweaks.common.gui.textures

import elan.tweaks.common.gui.drawing.TextureDrawer
import elan.tweaks.common.gui.geometry.Vector3D
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

abstract class ThaumcraftTextureInstance(
    path: String,
    private val textureWidth: Int,
    private val textureHeight: Int = textureWidth,
    private val u: Int,
    private val v: Int,
    override val width: Int,
    override val height: Int
) : TextureInstance {
    companion object {
        private const val DOMAIN = "thaumcraft"
    }

    private val resourceLocation = ResourceLocation(DOMAIN, path)
    private val textureManager by lazy { Minecraft.getMinecraft().textureManager }

    override fun draw(origin: Vector3D) {
        textureManager.bindTexture(resourceLocation)
        TextureDrawer.drawTexturedRectByParts(
            x0 = origin.x, y0 = origin.y, width = width, height = height, zLevel = origin.z,
            textureX = u, textureY = v, textureWidth = width, textureHeight = height, 
            textureXPartCount = textureWidth, textureYPartCount = textureHeight
        )
    }
}
