package com.example.smarttrafficradar.features.control

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.features.system.language.presentation.ui.ChangeLanguageActivity
import com.example.smarttrafficradar.features.system.language.presentation.viewmodel.LanguageViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.DarkBackground

@Composable
fun ControlScreen(
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val currentLang by languageViewModel.currentLanguage.collectAsState()
    var selectedLang by remember(currentLang) { mutableStateOf(currentLang) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = Dimen.PaddingXS)
                .padding(horizontal = Dimen.PaddingM)
        ) {
            ControlTopBar()

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            LanguagesCard(
                selectedLang = selectedLang,
                onChangeLanguage = {
                    val intent = Intent(context, ChangeLanguageActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}