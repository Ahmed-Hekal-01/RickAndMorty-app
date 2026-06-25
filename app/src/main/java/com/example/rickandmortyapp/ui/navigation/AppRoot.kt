package com.example.rickandmortyapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickandmortyapp.feature.auth.login.LoginScreen
import com.example.rickandmortyapp.feature.characterdetail.CharacterDetailsScreen
import com.example.rickandmortyapp.feature.home.HomeScreen
import com.example.rickandmortyapp.feature.profile.ProfileScreen
import com.example.rickandmortyapp.ui.components.BottomNavBar
import com.example.rickandmortyapp.util.AppGraphs
import com.example.rickandmortyapp.util.AppRoutes

@Composable
fun AppRoot(
    startDestination: String
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }
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
    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavBar(
                    selectedRoute = currentRoute ?: AppRoutes.HOME_SCREEN,
                    onNavigate = navigateMain
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
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
                        onShowSnackbar = { message ->
                            snackbarHostState.showSnackbar(message)
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
                composable(AppRoutes.HOME_SCREEN) {
                    HomeScreen(
                        onNavigateToCharacterDetails = { characterId ->
                            navController.navigate(
                                AppRoutes.CHARACTER_DETAILS_SCREEN.replace(
                                    "{characterId}",
                                    characterId.toString()
                                )
                            )
                        },
                        onShowSnackbar = { message ->
                            snackbarHostState.showSnackbar(message)
                        }
                    )
                }
                composable(AppRoutes.EPISODES_SCREEN) { }
                composable(
                    route = AppRoutes.CHARACTER_DETAILS_SCREEN,
                    arguments = listOf(navArgument("characterId") { type = NavType.IntType })
                ) {
                    CharacterDetailsScreen()
                }
                composable(AppRoutes.CHARACTER_EPISODE_SCREEN) { }
                composable(AppRoutes.FAV_SCREEN) { }
                composable(AppRoutes.SEARCH_SCREEN) { }
                composable(AppRoutes.PROFILE_SCREEN) {
                    ProfileScreen(
                        onNavigateToLogin = {
                            navController.navigate(AppGraphs.AUTH) {
                                popUpTo(AppGraphs.MAIN) { inclusive = true }
                            }
                        },
                        onShowSnackbar = { message ->
                            snackbarHostState.showSnackbar(message)
                        }
                    )
                }
            }
        }
    }
}