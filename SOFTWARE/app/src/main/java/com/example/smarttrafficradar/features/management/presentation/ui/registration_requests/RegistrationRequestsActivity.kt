package com.example.smarttrafficradar.features.management.presentation.ui.registration_requests

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.features.management.presentation.ui.register_card.RegisterCardScreen
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationRequestsActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "registration_requests"
            ) {
                composable("registration_requests") {
                    RegistrationRequestsScreen(
                        onBack = { finish() },
                        navController = navController
                    )
                }

                composable(
                    route = "register_card/{uid}/{vehicleType}/{timestamp}",
                    arguments = listOf(
                        navArgument("uid") { type = NavType.StringType },
                        navArgument("vehicleType") { type = NavType.StringType },
                        navArgument("timestamp") { type = NavType.LongType }
                    )
                ) { backStackEntry ->

                    RegisterCardScreen(
                        onBackClick = { navController.popBackStack() },
                        uid = backStackEntry.arguments?.getString("uid") ?: "",
                        vehicleType = VehicleType.valueOf(
                            backStackEntry.arguments?.getString("vehicleType")
                                ?: VehicleType.CAR.name
                        ),
                        timestamp = backStackEntry.arguments?.getLong("timestamp") ?: 0L
                    )
                }
            }
        }
    }
}