package com.example.smarttrafficradar

import android.content.Context
import androidx.activity.ComponentActivity
import com.example.smarttrafficradar.features.system.language.data.preference.LanguagePreferenceManager
import com.example.smarttrafficradar.features.system.language.domain.model.AppLanguage
import com.example.smarttrafficradar.utils.LangUtils
import com.example.smarttrafficradar.utils.LanguageManager
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

open class BaseComponentActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Lấy manager từ Hilt Singleton thay vì khởi tạo mới
        val entryPoint = EntryPoints.get(
            newBase.applicationContext,
            LanguageEntryPoint::class.java
        )
        val manager = entryPoint.getLanguagePreferenceManager()

        val updatedContext = runBlocking {
            // manager lúc này là instance duy nhất, không gây lỗi IllegalStateException
            val lang = manager.languageFlow.firstOrNull() ?: AppLanguage.ENGLISH
            val contextWithLocale = LanguageManager.setAppLocale(newBase, lang)

            LangUtils.currentLang = lang.code

            contextWithLocale
        }
        super.attachBaseContext(updatedContext)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface LanguageEntryPoint {
    fun getLanguagePreferenceManager(): LanguagePreferenceManager
}