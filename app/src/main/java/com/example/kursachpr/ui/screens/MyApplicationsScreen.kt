@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.kursachpr.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursachpr.data.model.Application
import com.example.kursachpr.data.model.ApplicationStatus
import com.example.kursachpr.data.model.Club
import com.example.kursachpr.ui.components.TopBar
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(
    viewModel: MainViewModel,
    onMenuClick: () -> Unit,
    onClubClick: (Long) -> Unit
) {
    val applications by viewModel.getMyApplications().collectAsState(initial = emptyList())
    val allClubs by viewModel.allClubs.collectAsState()

    // Создаём карту кружков для быстрого доступа
    val clubsMap = remember(allClubs) {
        allClubs.associateBy { it.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        TopBar(
            title = "Мои заявки",
            onMenuClick = onMenuClick
        )

        if (applications.isEmpty()) {
            // Пустое состояние
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
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = SecondaryColor,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Нет заявок",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Вы ещё не подавали заявки\nна посещение кружков",
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
                        text = "Всего заявок: ${applications.size}",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(applications, key = { it.id }) { application ->
                    val club = clubsMap[application.clubId]
                    ApplicationCard(
                        application = application,
                        club = club,
                        onClubClick = { club?.let { onClubClick(it.id) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplicationCard(
    application: Application,
    club: Club?,
    onClubClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClubClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = club?.name ?: "Кружок удалён",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatDate(application.createdAt),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                ApplicationStatusChip(status = application.status)
            }

            if (application.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Сообщение:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = application.message,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }

            club?.let {
                Spacer(modifier = Modifier.height(12.dp))
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
                        text = "${it.city}, ${it.address}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplicationStatusChip(status: ApplicationStatus) {
    val (backgroundColor, textColor, icon) = when (status) {
        ApplicationStatus.PENDING -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            Icons.Default.Schedule
        )
        ApplicationStatus.APPROVED -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            Icons.Default.CheckCircle
        )
        ApplicationStatus.REJECTED -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            Icons.Default.Cancel
        )
        ApplicationStatus.CANCELLED -> Triple(
            Color(0xFFF5F5F5),
            Color(0xFF757575),
            Icons.Default.Block
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy в HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

