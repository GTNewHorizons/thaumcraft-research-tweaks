package elan.tweaks.thaumcraft.research.domain.ports.required

interface ScribeTools {
    fun arePresentAndNotEmpty(): Boolean = !areMissingOrEmpty()
    fun areMissingOrEmpty(): Boolean
}
