package com.example.kursachpr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursachpr.data.model.Club
import com.example.kursachpr.data.model.ClubCategory
import com.example.kursachpr.ui.components.ClubCard
import com.example.kursachpr.ui.components.TopBar
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onMenuClick: () -> Unit,
    onClubClick: (Long) -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val allClubs by viewModel.allClubs.collectAsState()
    
    var showFilters by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Фильтры
    var selectedCity by remember { mutableStateOf("") }
    var selectedAge by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ClubCategory?>(null) }
    var selectedDistrict by remember { mutableStateOf("") }
    
    var categoryExpanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val displayClubs = if (searchResults.isNotEmpty() || searchQuery.isNotEmpty()) searchResults else allClubs
    val favoriteStates = remember { mutableStateMapOf<Long, Boolean>() }

    LaunchedEffect(displayClubs) {
        displayClubs.forEach { club ->
            favoriteStates[club.id] = viewModel.isFavorite(club.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        TopBar(
            title = "Поиск",
            onMenuClick = onMenuClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Поле поиска и кнопка города
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            if (it.isNotEmpty()) {
                                viewModel.searchByQuery(it)
                            }
                        },
                        placeholder = { Text("Поиск...", color = Color.Gray) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Поиск",
                                tint = PrimaryColor
                            )
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

                    OutlinedButton(
                        onClick = { selectedCity = "г. Муром" },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (selectedCity.isEmpty()) "Город" else selectedCity,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Блок фильтров
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SecondaryColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Фильтр поиска",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        // Возраст
                        FilterField(
                            label = "Возраст",
                            value = selectedAge,
                            onValueChange = { selectedAge = it },
                            placeholder = "Укажите возраст"
                        )

                        // Направление
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = !categoryExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory?.title ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Направление") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryColor,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                ClubCategory.entries.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.title) },
                                        onClick = {
                                            selectedCategory = category
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Район
                        FilterField(
                            label = "Район",
                            value = selectedDistrict,
                            onValueChange = { selectedDistrict = it },
                            placeholder = "Укажите район"
                        )

                        // Кнопка поиска
                        Button(
                            onClick = {
                                val age = selectedAge.toIntOrNull()
                                viewModel.searchClubs(
                                    city = selectedCity,
                                    category = selectedCategory,
                                    age = age
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CardBackground
                            )
                        ) {
                            Text(
                                text = "Поиск",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Результаты поиска
            items(displayClubs) { club ->
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
private fun FilterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )
    }
}


