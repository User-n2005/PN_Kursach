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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursachpr.data.model.Application as ClubApplication
import com.example.kursachpr.data.model.ApplicationStatus
import com.example.kursachpr.data.model.Club
import com.example.kursachpr.ui.components.TopBar
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClubApplicationsScreen(
    viewModel: MainViewModel,
    onMenuClick: () -> Unit
) {
    val applications by viewModel.getApplicationsForMyClubs().collectAsState(initial = emptyList<ClubApplication>())
    val allClubs by viewModel.allClubs.collectAsState()
    val scope = rememberCoroutineScope()

    // Создаём карту кружков
    val clubsMap = remember(allClubs) {
        allClubs.associateBy { it.id }
    }

    // Группируем заявки по статусу
    val pendingApplications = applications.filter { it.status == ApplicationStatus.PENDING }
    val processedApplications = applications.filter { it.status != ApplicationStatus.PENDING }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        TopBar(
            title = "Заявки на кружки",
            onMenuClick = onMenuClick
        )

        if (applications.isEmpty()) {
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
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        tint = SecondaryColor,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Заявок пока нет",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Когда пользователи начнут\nподавать заявки, они появятся здесь",
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
                if (pendingApplications.isNotEmpty()) {
                    item {
                        Text(
                            text = "Новые заявки (${pendingApplications.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(pendingApplications, key = { app -> app.id }) { application ->
                        OrganizerApplicationCard(
                            application = application,
                            club = clubsMap[application.clubId],
                            viewModel = viewModel,
                            onApprove = {
                                scope.launch {
                                    viewModel.updateApplicationStatus(application.id, ApplicationStatus.APPROVED)
                                }
                            },
                            onReject = {
                                scope.launch {
                                    viewModel.updateApplicationStatus(application.id, ApplicationStatus.REJECTED)
                                }
                            }
                        )
                    }
                }

                if (processedApplications.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Обработанные (${processedApplications.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(processedApplications, key = { app -> app.id }) { application ->
                        OrganizerApplicationCard(
                            application = application,
                            club = clubsMap[application.clubId],
                            viewModel = viewModel,
                            onApprove = null,
                            onReject = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrganizerApplicationCard(
    application: ClubApplication,
    club: Club?,
    viewModel: MainViewModel,
    onApprove: (() -> Unit)?,
    onReject: (() -> Unit)?
) {
    var applicantName by remember { mutableStateOf("Загрузка...") }
    
    LaunchedEffect(application.userId) {
        val user = viewModel.getUserById(application.userId)
        applicantName = user?.fullName ?: "Неизвестный пользователь"
    }

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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = applicantName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "На кружок: ${club?.name ?: "Удалён"}",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
                
                ApplicationStatusChip(status = application.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatDate(application.createdAt),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            if (application.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = BackgroundColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Сообщение:",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = application.message,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Кнопки действий для ожидающих заявок
            if (onApprove != null && onReject != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFC62828)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Отклонить")
                    }
                    
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Принять")
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicationStatusChip(status: ApplicationStatus) {
    val (backgroundColor, textColor) = when (status) {
        ApplicationStatus.PENDING -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
        ApplicationStatus.APPROVED -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        ApplicationStatus.REJECTED -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        ApplicationStatus.CANCELLED -> Pair(Color(0xFFF5F5F5), Color(0xFF757575))
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy в HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

