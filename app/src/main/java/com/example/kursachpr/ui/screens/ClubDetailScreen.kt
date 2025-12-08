package com.example.kursachpr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursachpr.data.model.*
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDetailScreen(
    viewModel: MainViewModel,
    clubId: Long,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val reviews by viewModel.getReviewsForClub(clubId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    var club by remember { mutableStateOf<Club?>(null) }
    var organizerName by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    
    var showApplicationDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    // Загрузка данных
    LaunchedEffect(clubId) {
        club = viewModel.getClubById(clubId)
        club?.let {
            val organizer = viewModel.getUserById(it.organizerId)
            organizerName = organizer?.fullName ?: "Неизвестный организатор"
        }
        isFavorite = viewModel.isFavorite(clubId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(club?.name ?: "Кружок", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (currentUser?.userType in listOf(UserType.PARENT, UserType.CHILD)) {
                        IconButton(onClick = {
                            scope.launch {
                                viewModel.toggleFavorite(clubId)
                                isFavorite = !isFavorite
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Избранное",
                                tint = if (isFavorite) Color.Red else Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
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
        } else if (club == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Кружок не найден", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundColor),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Заголовок с фото
                item {
                    ClubHeader(club = club!!, organizerName = organizerName)
                }

                // Информация
                item {
                    ClubInfoCard(club = club!!)
                }

                // Расписание
                item {
                    ScheduleCard(schedule = club!!.schedule)
                }

                // Кнопки действий для родителя/ребёнка
                if (currentUser?.userType in listOf(UserType.PARENT, UserType.CHILD)) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showReviewDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor)
                            ) {
                                Icon(Icons.Default.RateReview, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Отзыв")
                            }

                            Button(
                                onClick = { showApplicationDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Записаться")
                            }
                        }
                    }
                }

                // Отзывы
                item {
                    Text(
                        text = "Отзывы (${reviews.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                if (reviews.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Отзывов пока нет.\nБудьте первым!",
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(reviews, key = { it.id }) { review ->
                        ReviewCard(
                            review = review,
                            viewModel = viewModel,
                            isOrganizer = currentUser?.userType == UserType.ORGANIZER && 
                                         currentUser?.id == club?.organizerId
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Диалог заявки
    if (showApplicationDialog) {
        ApplicationDialog(
            onDismiss = { showApplicationDialog = false },
            onSubmit = { message ->
                scope.launch {
                    viewModel.submitApplication(clubId, message = message)
                    showApplicationDialog = false
                }
            }
        )
    }

    // Диалог отзыва
    if (showReviewDialog) {
        ReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, text ->
                scope.launch {
                    viewModel.addReview(clubId, rating, text)
                    showReviewDialog = false
                }
            }
        )
    }
}

@Composable
private fun ClubHeader(club: Club, organizerName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = club.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                if (club.isVerified) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Верифицирован",
                        tint = Color(0xFF4FC3F7),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = SecondaryColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = club.category.title,
                    fontSize = 12.sp,
                    color = PrimaryColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%.1f", club.rating),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = " (${club.reviewCount} отзывов)",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Организатор: $organizerName",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ClubInfoCard(club: Club) {
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
                text = "О кружке",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = club.description,
                fontSize = 14.sp,
                color = TextSecondary
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            InfoRow(icon = Icons.Default.LocationOn, label = "Адрес", value = "${club.city}, ${club.address}")
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(icon = Icons.Default.People, label = "Возраст", value = "${club.ageFrom} - ${club.ageTo} лет")
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(
                icon = Icons.Default.Payments,
                label = "Стоимость",
                value = if (club.pricePerMonth > 0) "${club.pricePerMonth} ₽/мес" else "Бесплатно"
            )
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
            Text(text = value, fontSize = 14.sp, color = TextPrimary)
        }
    }
}

@Composable
private fun ScheduleCard(schedule: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Расписание",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = schedule,
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ReviewCard(
    review: Review,
    viewModel: MainViewModel,
    isOrganizer: Boolean
) {
    var authorName by remember { mutableStateOf("") }
    var showReplyDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(review.userId) {
        val user = viewModel.getUserById(review.userId)
        authorName = user?.fullName ?: "Пользователь"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = authorName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.text,
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatDate(review.createdAt),
                fontSize = 12.sp,
                color = TextSecondary.copy(alpha = 0.6f)
            )

            // Ответ организатора
            if (review.reply.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = SecondaryColor.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Ответ организатора:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = review.reply,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Кнопка ответа для организатора
            if (isOrganizer && review.reply.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { showReplyDialog = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ответить", fontSize = 12.sp)
                }
            }
        }
    }

    // Диалог ответа
    if (showReplyDialog) {
        ReplyDialog(
            onDismiss = { showReplyDialog = false },
            onSubmit = { reply ->
                scope.launch {
                    viewModel.replyToReview(review.id, reply)
                    showReplyDialog = false
                }
            }
        )
    }
}

@Composable
private fun ApplicationDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var message by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Заявка на запись", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Оставьте сообщение для организатора (необязательно)",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Сообщение") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(message) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Отправить заявку")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Оставить отзыв", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Оценка:", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = index + 1 },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Ваш отзыв") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, text) },
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Отправить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ReplyDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var reply by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ответ на отзыв", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = reply,
                onValueChange = { reply = it },
                label = { Text("Ваш ответ") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(reply) },
                enabled = reply.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Отправить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = TextSecondary)
            }
        }
    )
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

