package elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures

import elan.tweaks.common.gui.dto.Scale
import elan.tweaks.common.gui.dto.UV
import elan.tweaks.common.gui.textures.ThaumcraftTextureInstance

object UsageHintTexture : ThaumcraftTextureInstance(
    "research/table/research-table.png",
    textureScale = Scale(310, 245),
    uv = UV(51, 220),
    uvScale = Scale.cube(ResearchTableInventoryTexture.UsageHint.SIZE_WITH_SHADOW_PIXELS)
)
