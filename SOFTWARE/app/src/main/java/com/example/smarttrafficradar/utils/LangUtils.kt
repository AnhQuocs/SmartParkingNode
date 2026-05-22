<<<<<<< HEAD
package com.example.smarttrafficradar.utils

import android.content.Context
import android.os.Build
import java.util.Locale

object LangUtils {
    var currentLang: String = "en"

    fun getLocalizedText(map: Map<String, String>?): String {
        return map?.get(currentLang) ?: map?.get("en") ?: ""
    }

    fun getLocalizedList(map: Map<String, List<String>>?): List<String> {
        return map?.get(currentLang) ?: emptyList()
    }

    fun updateLocale(context: Context, langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }

        currentLang = langCode
    }
=======
package com.example.smarttrafficradar.utils

import android.content.Context
import android.os.Build
import java.util.Locale

object LangUtils {
    var currentLang: String = "en"

    fun getLocalizedText(map: Map<String, String>?): String {
        return map?.get(currentLang) ?: map?.get("en") ?: ""
    }

    fun getLocalizedList(map: Map<String, List<String>>?): List<String> {
        return map?.get(currentLang) ?: emptyList()
    }

    fun updateLocale(context: Context, langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }

        currentLang = langCode
    }
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}