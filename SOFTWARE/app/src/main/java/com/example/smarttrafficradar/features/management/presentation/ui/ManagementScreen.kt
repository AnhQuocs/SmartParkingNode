package com.example.smarttrafficradar.features.management.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.theme.Background

@Composable
fun ManagementScreen() {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding()
                .verticalScroll(scrollState)
        ) {
            ManagementTopBar()

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            ManagementCategoriesSection(
                onRegistrationRequests = {

                },
                onRegisteredCardsClick = {

                },
                onUserListClick = {

                }
            )
        }
    }
}