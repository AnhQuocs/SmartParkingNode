package com.example.smarttrafficradar.features.management.presentation.ui.registerd_card

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.smarttrafficradar.BaseComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisteredCardsActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RegisteredCardsScreen(
                onBackClick = { finish() }
            )
        }
    }
}