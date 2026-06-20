package com.example.smarttrafficradar.features.main.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.components.AppAppearance
import com.example.smarttrafficradar.features.auth.presentation.ui.SignInScreen
import com.example.smarttrafficradar.features.auth.presentation.ui.SignUpScreen
import com.example.smarttrafficradar.features.main.viewmodel.MainViewModel
import com.example.smarttrafficradar.features.main.viewmodel.SplashViewModel
import com.example.smarttrafficradar.features.onboarding.presentation.ui.OnboardingScreen
import com.example.smarttrafficradar.features.user_profile.presentation.ui.CompleteProfileScreen
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gọi hàm lấy FCM Token và in ra Logcat ngay khi mở app
        getFcmTokenAndLog()

        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val themeState by viewModel.themeConfig.collectAsStateWithLifecycle()
            val isReady by viewModel.isReady.collectAsStateWithLifecycle()

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                // Handle results if needed
            }

            val goToAuth = intent.getBooleanExtra("GO_TO_AUTH", false)

            LaunchedEffect(Unit) {
                // Chuyển sang dùng MutableList để dễ dàng thêm quyền theo điều kiện phiên bản Android
                val permissionsToRequest = mutableListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                // Chỉ xin quyền thông báo nếu máy đang chạy Android 13 (TIRAMISU) trở lên
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)

                val allGranted = permissionsToRequest.all {
                    ContextCompat.checkSelfPermission(
                        context, it
                    ) == PackageManager.PERMISSION_GRANTED
                }

                if (!allGranted) {
                    // Chuyển list thành array để truyền vào launcher
                    permissionLauncher.launch(permissionsToRequest.toTypedArray())
                }
            }

            AppAppearance(themeConfig = themeState) {
                MainApp(gotoAuth = goToAuth)
            }
        }
    }

    // Hàm lấy token và in ra logcat
    private fun getFcmTokenAndLog() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TEST", "Lấy FCM token thất bại!", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM_TEST", "========================================")
            Log.d("FCM_TEST", "Token của máy này là: $token")
            Log.d("FCM_TEST", "========================================")
        }
    }
}

@Composable
fun MainApp(gotoAuth: Boolean) {
    val navController = rememberNavController()

    val startRoute = if (gotoAuth) "auth" else "splash_root"

    NavHost(
        navController = navController, startDestination = startRoute
    ) {
        splashGraph(navController)
        authGraph(navController)
        onboardingGraph(navController)
        profileCompletionGraph(navController)
        userGraph(navController)
        adminGraph(navController)
    }
}

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(
        startDestination = "sign_in", route = "auth"
    ) {
        composable("sign_in") {
            SignInScreen(navController = navController)
        }

        composable("sign_up") {
            SignUpScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.onboardingGraph(navController: NavController) {
    navigation(
        startDestination = "onboarding", route = "onboarding_root"
    ) {
        composable("onboarding") {
            OnboardingScreen(navController)
        }
    }
}

fun NavGraphBuilder.profileCompletionGraph(navController: NavController) {
    navigation(
        startDestination = "complete_profile", route = "profile_completion_root"
    ) {
        composable("complete_profile") {
            CompleteProfileScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.userGraph(navController: NavController) {
    navigation(
        startDestination = "user_main", route = "user_root"
    ) {
        composable("user_main") {
            UserMainScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.adminGraph(navController: NavController) {
    navigation(
        startDestination = "admin_main", route = "admin_root"
    ) {
        composable("admin_main") {
            AdminMainScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.splashGraph(navController: NavController) {
    navigation(
        startDestination = "splash", route = "splash_root"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
    }
}

@Composable
fun SplashScreen(
    navController: NavController, viewModel: SplashViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            style = TextStyle(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF26D9E8)
                    )
                )
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.decideStartDestination { destination ->
            navController.navigate(destination) {
                popUpTo("splash_root") { inclusive = true }
            }
        }
    }
}
