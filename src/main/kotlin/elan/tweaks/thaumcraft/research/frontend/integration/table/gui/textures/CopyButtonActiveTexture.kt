package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.UV
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ResearchTableInventoryTexture.CopyButton.SIZE_WITH_SHADOW_PIXELS

object CopyButtonActiveTexture :
    ThaumcraftTextureInstance(
        "research/table/research-table.png",
        textureScale = Scale(310, 245),
        uv = UV(26, 220),
        uvScale = Scale.cube(SIZE_WITH_SHADOW_PIXELS))
