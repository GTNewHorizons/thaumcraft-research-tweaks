package elan.tweaks.common.ext

object ResultExt {
  private val success = Result.success(Unit)

  fun success() = success
}
