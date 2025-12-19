package com.example.kursachpr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursachpr.data.model.Club
import com.example.kursachpr.ui.components.ClubCard
import com.example.kursachpr.ui.components.ClubCardSmall
import com.example.kursachpr.ui.components.TopBar
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onMenuClick: () -> Unit,
    onClubClick: (Long) -> Unit,
    onSearchClick: () -> Unit
) {
    val topClubs by viewModel.topClubs.collectAsState()
    val allClubs by viewModel.allClubs.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("г. Муром") }

    val scope = rememberCoroutineScope()
    val favoriteStates = remember { mutableStateMapOf<Long, Boolean>() }

    LaunchedEffect(allClubs) {
        allClubs.forEach { club ->
            favoriteStates[club.id] = viewModel.isFavorite(club.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        TopBar(
            title = "Главная",
            onMenuClick = onMenuClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TopClubsSection(
                    topClubs = topClubs,
                    onClubClick = onClubClick
                )
            }

            item {
                SearchSection(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    selectedCity = selectedCity,
                    onCityClick = { },
                    onSearchClick = onSearchClick
                )
            }

            items(allClubs) { club ->
                ClubCard(
                    club = club,
                    isFavorite = favoriteStates[club.id] ?: false,
                    onFavoriteClick = {
                        scope.launch {
                            viewModel.toggleFavorite(club.id)
                            favoriteStates[club.id] = !(favoriteStates[club.id] ?: false)
                        }
                    },
                    onClick = { onClubClick(club.id) }
                )
            }
        }
    }
}

@Composable
private fun TopClubsSection(
    topClubs: List<Club>,
    onClubClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Топ-3",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (topClubs.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "1",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    topClubs.getOrNull(0)?.let { club ->
                        ClubCardSmall(
                            club = club,
                            onClick = { onClubClick(club.id) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "2",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFC0C0C0),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    topClubs.getOrNull(1)?.let { club ->
                        ClubCardSmall(
                            club = club,
                            onClick = { onClubClick(club.id) },
                            modifier = Modifier.width(140.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "3",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFCD7F32),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    topClubs.getOrNull(2)?.let { club ->
                        ClubCardSmall(
                            club = club,
                            onClick = { onClubClick(club.id) },
                            modifier = Modifier.width(140.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCity: String,
    onCityClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Поиск кружков...", color = Color.Gray) },
            trailingIcon = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Поиск",
                        tint = PrimaryColor
                    )
                }
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SecondaryColor,
                unfocusedBorderColor = SecondaryColor,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Box(
            modifier = Modifier
                .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
                .background(Color.White, RoundedCornerShape(20.dp))
                .clickable { onCityClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = selectedCity,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}
