package elan.tweaks.thaumcraft.research.frontend.integration.proxies

import elan.tweaks.thaumcraft.research.frontend.integration.ThaumcraftResearchTweaks
import elan.tweaks.thaumcraft.research.frontend.integration.table.gui.layout.ParchmentHexMapLayout
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ClientSingletonInitializer : SingletonInitializer {
  private val logger: Logger = LogManager.getLogger(ThaumcraftResearchTweaks.MOD_ID)
  override fun initialize() {
    val parchmentHexMap = ParchmentHexMapLayout
    logger.debug("Initialised $parchmentHexMap")
  }
}
