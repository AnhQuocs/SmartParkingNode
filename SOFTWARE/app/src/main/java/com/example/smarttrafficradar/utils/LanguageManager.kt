<<<<<<< HEAD
package com.example.smarttrafficradar.utils

import android.content.Context
import android.os.Build
import android.os.LocaleList
import com.example.smarttrafficradar.features.system.language.domain.model.AppLanguage
import java.util.Locale

object LanguageManager {
    fun setAppLocale(context: Context, language: AppLanguage): Context {
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val config = context.resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            config.locale = locale
        }

        return context.createConfigurationContext(config)
    }
=======
package com.example.smarttrafficradar.utils

import android.content.Context
import android.os.Build
import android.os.LocaleList
import com.example.smarttrafficradar.features.system.language.domain.model.AppLanguage
import java.util.Locale

object LanguageManager {
    fun setAppLocale(context: Context, language: AppLanguage): Context {
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val config = context.resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            config.locale = locale
        }

        return context.createConfigurationContext(config)
    }
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}