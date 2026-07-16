package com.example.smarttrafficradar.features.profile.presentation.ui.admin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminProfileActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()

            val currentUser by authViewModel.currentUser.collectAsState()

            currentUser?.let {
                AdminProfileScreen(
                    user = it,
                    onBackClick = { finish() }
                )
            }
        }
    }
}