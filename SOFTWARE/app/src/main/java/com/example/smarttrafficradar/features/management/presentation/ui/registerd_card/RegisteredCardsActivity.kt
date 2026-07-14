package com.example.smarttrafficradar.features.management.presentation.ui.registerd_card

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.management.presentation.viewmodel.RegistrationListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisteredCardsActivity : BaseComponentActivity() {

    private val viewModel: RegistrationListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var selectedCard by remember {
                mutableStateOf<RegisteredCard?>(null)
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                RegisteredCardsScreen(
                    onBackClick = { finish() },
                    onClick = { card ->
                        selectedCard = card
                    },
                    registrationListViewModel = viewModel
                )

                selectedCard?.let { card ->
                    CardDetailScreen(
                        registeredCard = card,
                        onBackClick = {
                            selectedCard = null
                        },
                        onActive = { id ->
                            viewModel.updateCardStatus(id, CardStatus.ACTIVE)
                            selectedCard = null
                        },
                        onBlock = { id ->
                            viewModel.updateCardStatus(id, CardStatus.BLOCKED)
                            selectedCard = null
                        }
                    )
                }
            }
        }
    }
}