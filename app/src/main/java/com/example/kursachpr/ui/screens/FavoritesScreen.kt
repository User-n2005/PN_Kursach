package com.example.kursachpr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursachpr.data.model.Club
import com.example.kursachpr.ui.components.ClubCard
import com.example.kursachpr.ui.components.TopBar
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    onMenuClick: () -> Unit,
    onClubClick: (Long) -> Unit
) {
    val favorites by viewModel.getFavorites().collectAsState(initial = emptyList())
    val allClubs by viewModel.allClubs.collectAsState()
    val scope = rememberCoroutineScope()

    val favoriteClubs = remember(favorites, allClubs) {
        val favoriteIds = favorites.map { it.clubId }.toSet()
        allClubs.filter { it.id in favoriteIds }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        TopBar(
            title = "Избранное",
            onMenuClick = onMenuClick
        )

        if (favoriteClubs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = SecondaryColor,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "В избранном пока пусто",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Добавляйте понравившиеся кружки,\nчтобы не потерять их",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Сохранённые кружки: ${favoriteClubs.size}",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(favoriteClubs, key = { it.id }) { club ->
                    ClubCard(
                        club = club,
                        isFavorite = true,
                        onFavoriteClick = {
                            scope.launch {
                                viewModel.toggleFavorite(club.id)
                            }
                        },
                        onClick = { onClubClick(club.id) }
                    )
                }
            }
        }
    }
}
