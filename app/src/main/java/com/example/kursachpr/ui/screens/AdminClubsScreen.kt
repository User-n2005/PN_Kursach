@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.kursachpr.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursachpr.data.model.Club
import com.example.kursachpr.data.model.ClubCategory
import com.example.kursachpr.ui.components.TopBar
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminClubsScreen(
    viewModel: MainViewModel,
    onMenuClick: () -> Unit,
    onClubClick: (Long) -> Unit,
    onEditClub: (Long) -> Unit
) {
    val allClubs by viewModel.allClubs.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var showOnlyUnverified by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<ClubCategory?>(null) }

    // Фильтрация кружков
    val filteredClubs = remember(allClubs, searchQuery, showOnlyUnverified, selectedCategory) {
        allClubs.filter { club ->
            (searchQuery.isEmpty() || club.name.contains(searchQuery, ignoreCase = true)) &&
            (!showOnlyUnverified || !club.isVerified) &&
            (selectedCategory == null || club.category == selectedCategory)
        }
    }

    val unverifiedCount = allClubs.count { !it.isVerified }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        TopBar(
            title = "Управление кружками",
            onMenuClick = onMenuClick
        )

        // Поиск
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Поиск по названию") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedContainerColor = Color.White
            )
        )

        // Переключатель неверифицированных
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = showOnlyUnverified,
                onClick = { showOnlyUnverified = !showOnlyUnverified },
                label = { 
                    Text("Без верификации ($unverifiedCount)")
                },
                leadingIcon = if (showOnlyUnverified) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentColor,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }

        // Статистика
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Показано: ${filteredClubs.size} из ${allClubs.size}",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredClubs, key = { it.id }) { club ->
                AdminClubCard(
                    club = club,
                    viewModel = viewModel,
                    onClick = { onClubClick(club.id) },
                    onEdit = { onEditClub(club.id) },
                    onToggleVerification = {
                        scope.launch {
                            val newStatus = !club.isVerified
                            viewModel.setClubVerified(club.id, newStatus)
                            val message = if (newStatus) {
                                "Кружок \"${club.name}\" верифицирован ✓"
                            } else {
                                "Верификация кружка \"${club.name}\" снята"
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDelete = {
                        scope.launch {
                            viewModel.deleteClub(club.id)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AdminClubCard(
    club: Club,
    viewModel: MainViewModel,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onToggleVerification: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var organizerName by remember { mutableStateOf("Загрузка...") }

    LaunchedEffect(club.organizerId) {
        val user = viewModel.getUserById(club.organizerId)
        organizerName = user?.fullName ?: "Неизвестный"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column {
            // Заголовок с информацией о верификации
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (club.isVerified) Color(0xFF4CAF50) else CardBackground)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = club.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (club.isVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Верифицирован",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Кнопка верификации
                    IconButton(
                        onClick = onToggleVerification,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (club.isVerified) 
                                Icons.Default.RemoveCircle 
                            else 
                                Icons.Default.CheckCircle,
                            contentDescription = if (club.isVerified) "Снять верификацию" else "Верифицировать",
                            tint = Color.White
                        )
                    }
                }
            }

            // Содержимое
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Категория
                Surface(
                    color = SecondaryColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = club.category.title,
                        fontSize = 12.sp,
                        color = PrimaryColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = club.description,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Организатор
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Организатор: $organizerName",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Адрес
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${club.city}, ${club.address}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Рейтинг и цена
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", club.rating),
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = " (${club.reviewCount})",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Text(
                        text = if (club.pricePerMonth > 0) "${club.pricePerMonth} ₽/мес" else "Бесплатно",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (club.pricePerMonth > 0) AccentColor else Color(0xFF2E7D32)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Кнопки редактирования и удаления
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onEdit,
                        colors = ButtonDefaults.textButtonColors(contentColor = PrimaryColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Изменить")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Удалить")
                    }
                }
            }
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить кружок?") },
            text = {
                Text(
                    "Вы уверены, что хотите удалить кружок \"${club.name}\"? " +
                    "Все связанные данные (отзывы, заявки) также будут удалены."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

