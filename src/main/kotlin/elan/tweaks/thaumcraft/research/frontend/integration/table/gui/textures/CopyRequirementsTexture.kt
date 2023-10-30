package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.UV
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance

object CopyRequirementsTexture :
    ThaumcraftTextureInstance(
        "gui/guiresearchtable2.png",
        textureScale = Scale.cube(256),
        uv = UV(184, 224),
        uvScale = Scale(width = 48, height = 16))
