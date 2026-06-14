package app.marlboroadvance.mpvex.utils

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate

/**
 * Wraps this context so resources resolve against the in-app locale chosen via
 * [AppCompatDelegate.setApplicationLocales]. Needed for application/service
 * contexts on Android 8–12, where the AppCompat per-app-locale backport only
 * localizes Activity contexts. No-op when the app follows the system locale,
 * and harmless on Android 13+ where the framework localizes every context.
 *
 * Call at string-resolution time rather than caching the wrapped context, so a
 * language change is picked up immediately.
 */
fun Context.withAppLocale(): Context {
  val locales = AppCompatDelegate.getApplicationLocales()
  if (locales.isEmpty) return this
  val config = Configuration(resources.configuration)
  config.setLocales(locales.unwrap() as LocaleList)
  return createConfigurationContext(config)
}
