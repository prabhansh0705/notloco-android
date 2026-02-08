package com.notloco.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notloco.android.data.repository.AuthRepository
import com.notloco.android.ui.screens.auth.LoginScreen
import com.notloco.android.ui.screens.auth.OTPScreen
import com.notloco.android.ui.screens.auth.SignupScreen
import com.notloco.android.ui.screens.launch.LaunchScreen
import com.notloco.android.ui.screens.home.HomeScreen

sealed class Screen(val route: String) {
    object Launch : Screen("launch")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object OTP : Screen("otp/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "otp/${java.net.URLEncoder.encode(phoneNumber, "UTF-8")}"
    }
    object Home : Screen("home")
    object Journal : Screen("journal")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
}

@Composable
fun NotLocoNavGraph(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository = hiltViewModel<AuthViewModel>().authRepository
) {
    val isLoggedIn by authRepository.isLoggedIn().collectAsState(initial = false)
    
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Launch.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Launch.route) {
            LaunchScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Launch.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Screen.Signup.route) {
                        popUpTo(Screen.Launch.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Launch.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignup = {
                    navController.navigate(Screen.Signup.route)
                },
                onNavigateToOTP = { phoneNumber ->
                    navController.navigate(Screen.OTP.createRoute(phoneNumber))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onNavigateToOTP = { phoneNumber ->
                    navController.navigate(Screen.OTP.createRoute(phoneNumber))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.OTP.route,
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phoneNumber = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("phoneNumber") ?: "", 
                "UTF-8"
            )
            
            OTPScreen(
                phoneNumber = phoneNumber,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}

// Helper ViewModel to access AuthRepository
@dagger.hilt.android.lifecycle.HiltViewModel
class AuthViewModel @javax.inject.Inject constructor(
    val authRepository: AuthRepository
) : androidx.lifecycle.ViewModel()
