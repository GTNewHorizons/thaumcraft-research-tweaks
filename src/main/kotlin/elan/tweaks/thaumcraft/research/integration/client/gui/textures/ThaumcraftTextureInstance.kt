package elan.tweaks.thaumcraft.research.integration.client.gui.textures

import cpw.mods.fml.client.config.GuiUtils
import elan.tweaks.thaumcraft.research.integration.client.gui.Vector
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

abstract class ThaumcraftTextureInstance(
    path: String,
    val u: Int,
    val v: Int,
    override val width: Int,
    override val height: Int,
) : TextureInstance {
    companion object {
        private const val DOMAIN = "thaumcraft"
    }

    private val resourceLocation = ResourceLocation(DOMAIN, path)
    private val textureManager by lazy { Minecraft.getMinecraft().textureManager }

    override fun draw(origin: Vector, zLevel: Float) {
        textureManager.bindTexture(resourceLocation)
        GuiUtils.drawTexturedModalRect(origin.x, origin.y, u, v, width, height, zLevel)
    }
}
