package com.example.kursachpr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursachpr.data.model.Club
import com.example.kursachpr.data.model.ClubCategory
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditClubScreen(
    viewModel: MainViewModel,
    clubId: Long? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val scope = rememberCoroutineScope()
    
    var isLoading by remember { mutableStateOf(clubId != null) }
    var existingClub by remember { mutableStateOf<Club?>(null) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ClubCategory.OTHER) }
    var city by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var ageFrom by remember { mutableStateOf("") }
    var ageTo by remember { mutableStateOf("") }
    var pricePerMonth by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var categoryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(clubId) {
        if (clubId != null) {
            val club = viewModel.getClubById(clubId)
            club?.let {
                existingClub = it
                name = it.name
                description = it.description
                selectedCategory = it.category
                city = it.city
                district = it.district
                address = it.address
                ageFrom = it.ageFrom.toString()
                ageTo = it.ageTo.toString()
                pricePerMonth = it.pricePerMonth.toString()
                schedule = it.schedule
                imageUrl = it.imageUrl
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (clubId == null) "Создание кружка" else "Редактирование",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundColor)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionCard(title = "Основная информация") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Название кружка *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Описание *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.title,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Направление *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = textFieldColors()
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
                }

                SectionCard(title = "Местоположение") {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Город *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("Район") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Адрес *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors()
                    )
                }

                SectionCard(title = "Параметры") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = ageFrom,
                            onValueChange = { if (it.all { c -> c.isDigit() }) ageFrom = it },
                            label = { Text("Возраст от *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = textFieldColors()
                        )

                        OutlinedTextField(
                            value = ageTo,
                            onValueChange = { if (it.all { c -> c.isDigit() }) ageTo = it },
                            label = { Text("Возраст до *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = textFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pricePerMonth,
                        onValueChange = { if (it.all { c -> c.isDigit() }) pricePerMonth = it },
                        label = { Text("Стоимость в месяц (₽)") },
                        placeholder = { Text("0 - бесплатно") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldColors()
                    )
                }

                SectionCard(title = "Расписание") {
                    OutlinedTextField(
                        value = schedule,
                        onValueChange = { schedule = it },
                        label = { Text("Расписание занятий *") },
                        placeholder = { Text("Пн, Ср, Пт: 15:00-17:00") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        colors = textFieldColors()
                    )
                }

                SectionCard(title = "Фото (необязательно)") {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("URL изображения") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        currentUser?.let { user ->
                            scope.launch {
                                val club = Club(
                                    id = existingClub?.id ?: 0,
                                    organizerId = user.id,
                                    name = name.trim(),
                                    description = description.trim(),
                                    category = selectedCategory,
                                    city = city.trim(),
                                    district = district.trim(),
                                    address = address.trim(),
                                    ageFrom = ageFrom.toIntOrNull() ?: 0,
                                    ageTo = ageTo.toIntOrNull() ?: 18,
                                    pricePerMonth = pricePerMonth.toIntOrNull() ?: 0,
                                    schedule = schedule.trim(),
                                    imageUrl = imageUrl.trim(),
                                    isVerified = existingClub?.isVerified ?: false,
                                    isActive = existingClub?.isActive ?: true,
                                    rating = existingClub?.rating ?: 0f,
                                    reviewCount = existingClub?.reviewCount ?: 0,
                                    createdAt = existingClub?.createdAt ?: System.currentTimeMillis()
                                )

                                if (clubId == null) {
                                    viewModel.insertClub(club)
                                } else {
                                    viewModel.updateClub(club)
                                }
                                onSaved()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    enabled = name.isNotBlank() && description.isNotBlank() && 
                              city.isNotBlank() && address.isNotBlank() && 
                              ageFrom.isNotBlank() && ageTo.isNotBlank() && schedule.isNotBlank()
                ) {
                    Icon(
                        imageVector = if (clubId == null) Icons.Default.Add else Icons.Default.Save,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (clubId == null) "Создать кружок" else "Сохранить изменения",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryColor,
    focusedLabelColor = PrimaryColor,
    cursorColor = PrimaryColor
)
