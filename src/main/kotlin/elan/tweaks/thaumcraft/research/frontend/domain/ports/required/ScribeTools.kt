package elan.tweaks.thaumcraft.research.frontend.domain.ports.required

interface ScribeTools {
    fun arePresentAndNotEmpty(): Boolean = !areMissingOrEmpty()
    fun areMissingOrEmpty(): Boolean
}
