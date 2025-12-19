package com.example.kursachpr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kursachpr.data.database.AppDatabase
import com.example.kursachpr.ui.components.DrawerMenu
import com.example.kursachpr.ui.screens.*
import com.example.kursachpr.ui.theme.KursachTheme
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KursachTheme {
                val context = LocalContext.current
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                
                LaunchedEffect(Unit) {
                    val database = AppDatabase.getDatabase(context)
                    AppDatabase.ensurePopulated(database)
                }
                
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
                                        popUpTo(Screen.Home) { inclusive = false }
                                    }
                                }
                            },
                            onLogout = {
                                scope.launch {
                                    drawerState.close()
                                    viewModel.logout()
                                    navController.navigate(Screen.Login) {
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
                            composable(Screen.Login) {
                                LoginScreen(
                                    navController = navController,
                                    viewModel = viewModel
                                )
                            }

                            composable(Screen.Registration) {
                                RegistrationScreen(
                                    navController = navController,
                                    viewModel = viewModel
                                )
                            }

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

                            composable("${Screen.ClubDetail}/{clubId}") { backStackEntry ->
                                val clubId = backStackEntry.arguments?.getString("clubId")?.toLongOrNull() ?: 0L
                                ClubDetailScreen(
                                    viewModel = viewModel,
                                    clubId = clubId,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Profile) {
                                ProfileScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onNavigateToChildren = {
                                        navController.navigate(Screen.Children)
                                    },
                                    onNavigateToFavorites = {
                                        navController.navigate(Screen.Favorites)
                                    },
                                    onNavigateToApplications = {
                                        navController.navigate(Screen.MyApplications)
                                    },
                                    onNavigateToMyClubs = {
                                        navController.navigate(Screen.MyClubs)
                                    },
                                    onNavigateToClubApplications = {
                                        navController.navigate(Screen.ClubApplications)
                                    },
                                    onNavigateToAdminUsers = {
                                        navController.navigate(Screen.AdminUsers)
                                    },
                                    onNavigateToAdminClubs = {
                                        navController.navigate(Screen.AdminClubs)
                                    },
                                    onNavigateToAdminReviews = {
                                        navController.navigate(Screen.AdminReviews)
                                    }
                                )
                            }

                            composable(Screen.Favorites) {
                                FavoritesScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onClubClick = { clubId ->
                                        navController.navigate("${Screen.ClubDetail}/$clubId")
                                    }
                                )
                            }

                            composable(Screen.MyApplications) {
                                MyApplicationsScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onClubClick = { clubId ->
                                        navController.navigate("${Screen.ClubDetail}/$clubId")
                                    }
                                )
                            }

                            composable(Screen.Children) {
                                ChildrenScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    }
                                )
                            }

                            composable(Screen.MyClubs) {
                                MyClubsScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onCreateClub = {
                                        navController.navigate(Screen.CreateClub)
                                    },
                                    onEditClub = { clubId ->
                                        navController.navigate("${Screen.EditClub}/$clubId")
                                    },
                                    onClubClick = { clubId ->
                                        navController.navigate("${Screen.ClubDetail}/$clubId")
                                    }
                                )
                            }

                            composable(Screen.ClubApplications) {
                                ClubApplicationsScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    }
                                )
                            }

                            composable(Screen.CreateClub) {
                                CreateEditClubScreen(
                                    viewModel = viewModel,
                                    clubId = null,
                                    onBack = { navController.popBackStack() },
                                    onSaved = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable("${Screen.EditClub}/{clubId}") { backStackEntry ->
                                val clubId = backStackEntry.arguments?.getString("clubId")?.toLongOrNull()
                                CreateEditClubScreen(
                                    viewModel = viewModel,
                                    clubId = clubId,
                                    onBack = { navController.popBackStack() },
                                    onSaved = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable(Screen.AdminUsers) {
                                AdminUsersScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    }
                                )
                            }

                            composable(Screen.AdminClubs) {
                                AdminClubsScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onClubClick = { clubId ->
                                        navController.navigate("${Screen.ClubDetail}/$clubId")
                                    },
                                    onEditClub = { clubId ->
                                        navController.navigate("${Screen.EditClub}/$clubId")
                                    }
                                )
                            }

                            composable(Screen.AdminReviews) {
                                AdminReviewsScreen(
                                    viewModel = viewModel,
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

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
    const val ClubApplications = "club_applications"
    const val CreateClub = "create_club"
    const val EditClub = "edit_club"
    const val AdminUsers = "admin_users"
    const val AdminClubs = "admin_clubs"
    const val AdminReviews = "admin_reviews"
}
