package com.example.rickandmortyapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickandmortyapp.feature.auth.login.LoginScreen
import com.example.rickandmortyapp.feature.auth.register.RegistrationScreen
import com.example.rickandmortyapp.feature.characterdetail.CharacterDetailsScreen
import com.example.rickandmortyapp.feature.characterdetail.CharacterEpisodesScreen
import com.example.rickandmortyapp.feature.favorite.FavoriteScreen
import com.example.rickandmortyapp.feature.episodes.EpisodesScreen
import com.example.rickandmortyapp.feature.home.HomeScreen
import com.example.rickandmortyapp.feature.profile.ProfileScreen
import com.example.rickandmortyapp.ui.components.BottomNavBar
import com.example.rickandmortyapp.util.AppGraphs
import com.example.rickandmortyapp.util.AppRoutes
import com.example.rickandmortyapp.feature.auth.forgot.ForgotPasswordScreen

@Composable
fun AppRoot(
    startDestination: String
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showSnackbar: suspend (String) -> Unit = { message ->
        snackbarHostState.currentSnackbarData?.dismiss()
        val job = scope.launch {
            delay(1000)
            snackbarHostState.currentSnackbarData?.dismiss()
        }
        snackbarHostState.showSnackbar(message)
        job.cancel()
    }
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
                            showSnackbar(message)
                        }

                    )
                }
                composable(AppRoutes.SIGN_UP_SCREEN) {
                    RegistrationScreen(
                        onNavigateToHome = {
                            navController.navigate(AppGraphs.MAIN) {
                                popUpTo(AppGraphs.AUTH) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.navigate(AppRoutes.LOGIN_SCREEN) {
                                popUpTo(AppRoutes.SIGN_UP_SCREEN) { inclusive = true }
                            }
                        },
                        onShowSnackbar = { message ->
                            showSnackbar(message)
                        }
                    )
                }
                composable(AppRoutes.FORGOT_PASSWORD_SCREEN) {
                    ForgotPasswordScreen(
                        onBackClick = {
                            navController.navigate(AppRoutes.LOGIN_SCREEN) {
                                popUpTo(AppRoutes.FORGOT_PASSWORD_SCREEN) { inclusive = true }
                            }
                        },
                        onShowSnackbar = { message ->
                            showSnackbar(message)
                        }
                    )
                }

            }

            navigation(
                startDestination = AppRoutes.HOME_SCREEN, route = AppGraphs.MAIN
            ) {
                composable(AppRoutes.HOME_SCREEN) {
                    HomeScreen(
                        onNavigateToCharacterDetails = { characterId ->
                            val route = AppRoutes.CHARACTER_DETAILS_SCREEN.replace(
                                "{characterId}",
                                characterId.toString()
                            )
                            if (navController.currentBackStackEntry?.destination?.route != AppRoutes.CHARACTER_DETAILS_SCREEN) {
                                navController.navigate(route)
                            }
                        },
                        onShowSnackbar = { message ->
                            showSnackbar(message)
                        }
                    )
                }
                composable(AppRoutes.EPISODES_SCREEN) {
                    EpisodesScreen(
                        onNavClick = { route ->
                            navigateMain(route)
                        },
                        onShowSnackbar = { message ->
                            showSnackbar(message)
                        }
                    )
                }
                composable(
                    route = AppRoutes.CHARACTER_DETAILS_SCREEN,
                    arguments = listOf(navArgument("characterId") { type = NavType.IntType })
                ) {
                    CharacterDetailsScreen(
                        onNavigateBack = {
                            navController.navigateUp()
                        },
                        onNavigateToEpisodes = { characterId ->
                            val route = AppRoutes.CHARACTER_EPISODE_SCREEN.replace(
                                "{characterId}",
                                characterId.toString()
                            )
                            if (navController.currentBackStackEntry?.destination?.route != AppRoutes.CHARACTER_EPISODE_SCREEN) {
                                navController.navigate(route)
                            }
                        },
                        onShowSnackbar = { message ->
                            showSnackbar(message)
                        }
                    )
                }
                composable(
                    route = AppRoutes.CHARACTER_EPISODE_SCREEN,
                    arguments = listOf(navArgument("characterId") { type = NavType.IntType })
                ) {
                    CharacterEpisodesScreen(
                        onNavigateBack = {
                            navController.navigateUp()
                        },
                        onShowSnackbar = { message ->
                            showSnackbar(message)
                        }
                    )
                }
                composable(AppRoutes.FAV_SCREEN) {
                    FavoriteScreen(
                        onNavigateToHome = {
                            navigateMain(AppRoutes.HOME_SCREEN)
                        },
                        onShowSnackbar = { message ->
                            showSnackbar(message)
                        }
                    )
                }
                composable(AppRoutes.SEARCH_SCREEN) { }
                composable(AppRoutes.PROFILE_SCREEN) {
                    ProfileScreen(
                        onNavigateToLogin = {
                            navController.navigate(AppGraphs.AUTH) {
                                popUpTo(AppGraphs.MAIN) { inclusive = true }
                            }
                        },
                        onShowSnackbar = { message ->
                            showSnackbar(message)
                        }
                    )
                }
            }
        }
    }
}