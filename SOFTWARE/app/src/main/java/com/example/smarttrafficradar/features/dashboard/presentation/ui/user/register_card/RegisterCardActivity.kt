package com.example.smarttrafficradar.features.dashboard.presentation.ui.user.register_card

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.smarttrafficradar.BaseComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterCardActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uid = intent.getStringExtra("uid") ?: ""

        enableEdgeToEdge()
        setContent {
            RegisterCardScreen(
                uid = uid,
                onBack = { finish() }
            )
        }
    }
}