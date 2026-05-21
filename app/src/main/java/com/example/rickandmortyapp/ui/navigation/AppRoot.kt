package com.example.rickandmortyapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.rickandmortyapp.ui.components.BottomNavBar
import com.example.rickandmortyapp.ui.screens.LoginScreen
import com.example.rickandmortyapp.ui.screens.SplashScreen
import com.example.rickandmortyapp.util.AppGraphs
import com.example.rickandmortyapp.util.AppRoutes

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val navigateMain: (String) -> Unit = { route ->
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(AppRoutes.HOME_SCREEN) { saveState = true }
        }
    }
    val bottomBarRoutes = setOf(
        AppRoutes.HOME_SCREEN,
        AppRoutes.EPISODES_SCREEN,
        AppRoutes.FAV_SCREEN,
        AppRoutes.SEARCH_SCREEN,
        AppRoutes.PROFILE_SCREEN
    )
    Scaffold(bottomBar = {
        if (currentRoute in bottomBarRoutes) {
            BottomNavBar(
                selectedRoute = currentRoute ?: AppRoutes.HOME_SCREEN,
                onNavigate = navigateMain
            )
        }
    }) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = AppRoutes.SPLASH_SCREEN,
            modifier = Modifier.padding(paddingValues)
        ) {
            navigation(
                startDestination = AppRoutes.LOGIN_SCREEN, route = AppGraphs.AUTH
            ) {
                composable(AppRoutes.LOGIN_SCREEN) {
                    LoginScreen(
                        onNavigateToHome = {
                            navController.navigate(AppGraphs.MAIN) {
                                popUpTo(AppGraphs.AUTH) { inclusive = true }
                            }
                        },
                        onNavigateToSignUp = {
                            navController.navigate(AppRoutes.SIGN_UP_SCREEN)
                        },
                        onNavigateToForgetPassword = {
                            navController.navigate(AppRoutes.FORGOT_PASSWORD_SCREEN)
                        },
                        onShowSnackbar = {
                            // todo
                        }
                    )
                }
                composable(AppRoutes.SIGN_UP_SCREEN) { }
                composable(AppRoutes.FORGOT_PASSWORD_SCREEN) { }
                composable(AppRoutes.OTP_VERIFICATION_SCREEN) { }
                composable(AppRoutes.NEW_PASSWORD_SCREEN) { }

            }

            navigation(
                startDestination = AppRoutes.HOME_SCREEN, route = AppGraphs.MAIN
            ) {
                composable(AppRoutes.HOME_SCREEN) { }
                composable(AppRoutes.EPISODES_SCREEN) { }
                composable(AppRoutes.CHARACTER_DETAILS_SCREEN) { }
                composable(AppRoutes.CHARACTER_EPISODE_SCREEN) { }
                composable(AppRoutes.FAV_SCREEN) { }
                composable(AppRoutes.SEARCH_SCREEN) { }
                composable(AppRoutes.PROFILE_SCREEN) { }
            }
            composable(AppRoutes.SPLASH_SCREEN) {
                SplashScreen(onNavigateToLogin = {
                    navController.navigate(AppGraphs.AUTH) {
                        popUpTo(AppRoutes.SPLASH_SCREEN) { inclusive = true }
                    }
                }, onNavigateToHome = {
                    navController.navigate(AppGraphs.MAIN) {
                        popUpTo(AppRoutes.SPLASH_SCREEN) { inclusive = true }
                    }
                })
            }
        }
    }
}