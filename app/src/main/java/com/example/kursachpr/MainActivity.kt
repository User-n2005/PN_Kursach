package com.example.kursachpr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kursachpr.ui.components.DrawerMenu
import com.example.kursachpr.ui.screens.HomeScreen
import com.example.kursachpr.ui.screens.SearchScreen
import com.example.kursachpr.ui.theme.KursachTheme
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KursachTheme {
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                
                val currentUser by viewModel.currentUser.collectAsState()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = currentUser != null,
                    drawerContent = {
                        DrawerMenu(
                            userType = currentUser?.userType,
                            onItemClick = { route ->
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(route) {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            },
                            onLogout = {
                                scope.launch {
                                    drawerState.close()
                                    viewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Login
                        ) {
                            // Экран входа
                            composable(Screen.Login) {
                                LoginScreen(
                                    navController = navController,
                                    viewModel = viewModel
                                )
                            }

                            // Экран регистрации
                            composable(Screen.Registration) {
                                RegistrationScreen(
                                    navController = navController,
                                    viewModel = viewModel
                                )
                            }

                            // Главная страница
                            composable(Screen.Home) {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onClubClick = { clubId ->
                                        navController.navigate("${Screen.ClubDetail}/$clubId")
                                    },
                                    onSearchClick = {
                                        navController.navigate(Screen.Search)
                                    }
                                )
                            }

                            // Экран поиска
                            composable(Screen.Search) {
                                SearchScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onClubClick = { clubId ->
                                        navController.navigate("${Screen.ClubDetail}/$clubId")
                                    }
                                )
                            }

                            // Детальная страница кружка (пока заглушка)
                            composable("${Screen.ClubDetail}/{clubId}") { backStackEntry ->
                                val clubId = backStackEntry.arguments?.getString("clubId")?.toLongOrNull()
                                // TODO: ClubDetailScreen
                                Text("Страница кружка $clubId")
                            }

                            // Личный кабинет (пока заглушка)
                            composable(Screen.Profile) {
                                Text("Личный кабинет")
                            }

                            // Избранное (пока заглушка)
                            composable(Screen.Favorites) {
                                Text("Избранное")
                            }

                            // Мои записи (пока заглушка)
                            composable(Screen.MyApplications) {
                                Text("Мои записи")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Маршруты навигации
object Screen {
    const val Registration = "registration"
    const val Login = "login"
    const val Home = "home"
    const val Search = "search"
    const val ClubDetail = "club_detail"
    const val Profile = "profile"
    const val Favorites = "favorites"
    const val MyApplications = "my_applications"
    const val Children = "children"
    const val MyClubs = "my_clubs"
    const val Applications = "applications"
    const val AdminUsers = "admin_users"
    const val AdminClubs = "admin_clubs"
    const val AdminReviews = "admin_reviews"
}
