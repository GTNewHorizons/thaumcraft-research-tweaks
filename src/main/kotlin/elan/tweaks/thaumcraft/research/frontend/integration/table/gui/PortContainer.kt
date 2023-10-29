package elan.tweaks.thaumcraft.research.frontend.integration.table.gui

import elan.tweaks.thaumcraft.research.frontend.domain.model.AspectPallet
import elan.tweaks.thaumcraft.research.frontend.domain.model.AspectTree
import elan.tweaks.thaumcraft.research.frontend.domain.model.ResearchProcess
import elan.tweaks.thaumcraft.research.frontend.domain.model.ResearcherKnowledge
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectPalletPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.AspectsTreePort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearchProcessPort
import elan.tweaks.thaumcraft.research.frontend.domain.ports.provided.ResearcherKnowledgePort
import elan.tweaks.thaumcraft.research.frontend.integration.adapters.*
import elan.tweaks.thaumcraft.research.frontend.integration.table.container.ResearchTableContainerFactory
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.textures.ResearchTableInventoryTexture
import net.minecraft.entity.player.EntityPlayer
import thaumcraft.common.tiles.TileResearchTable

class PortContainer(player: EntityPlayer, table: TileResearchTable) {
  val inventory =
      ResearchTableContainerFactory.create(
          playerInventory = player.inventory,
          table = table,
          scribeToolsSlotOrigin = ResearchTableInventoryTexture.Slots.scribeToolsOrigin,
          notesSlotOrigin = ResearchTableInventoryTexture.Slots.notesOrigin,
          inventoryUiOrigin = ResearchTableInventoryTexture.inventoryOrigin,
      )

  private val base = KnowledgeBaseAdapter(player)
  private val pool = AspectPoolAdapter(player, table)
  private val combiner = AspectCombinerAdapter(player, table)
  private val notes = ResearchNotesAdapter(player, table, inventory)
  private val scribeTools = ScribeToolsAdapter(table)
  private val playerInventory = PlayerInventoryAdapter(player)

  val pallet: AspectPalletPort =
      AspectPallet(base = base, pool = pool, combiner = combiner, batchSize = 10)

  val tree: AspectsTreePort = AspectTree

  val researcher: ResearcherKnowledgePort =
      ResearcherKnowledge(
          base = base,
          pool = pool,
      )

  val research: ResearchProcessPort =
      ResearchProcess(
          pool = pool,
          notes = notes,
          scribeTools = scribeTools,
          knowledgeBase = base,
          playerInventory = playerInventory)
}
