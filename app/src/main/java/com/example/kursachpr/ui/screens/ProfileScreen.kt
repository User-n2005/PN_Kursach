@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.kursachpr.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursachpr.data.model.User
import com.example.kursachpr.data.model.UserType
import com.example.kursachpr.ui.components.TopBar
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onMenuClick: () -> Unit,
    onNavigateToChildren: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToApplications: () -> Unit = {},
    onNavigateToMyClubs: () -> Unit = {},
    onNavigateToClubApplications: () -> Unit = {},
    onNavigateToAdminUsers: () -> Unit = {},
    onNavigateToAdminClubs: () -> Unit = {},
    onNavigateToAdminReviews: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        TopBar(
            title = "Личный кабинет",
            onMenuClick = onMenuClick
        )

        currentUser?.let { user ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Аватар и имя
                ProfileHeader(user = user)

                Spacer(modifier = Modifier.height(24.dp))

                // Информация о пользователе
                ProfileInfoCard(user = user)

                Spacer(modifier = Modifier.height(16.dp))

                // Быстрые действия в зависимости от типа пользователя
                when (user.userType) {
                    UserType.USER -> ParentQuickActions(
                        onChildrenClick = onNavigateToChildren,
                        onFavoritesClick = onNavigateToFavorites,
                        onApplicationsClick = onNavigateToApplications
                    )
                    UserType.CHILD -> ChildQuickActions(
                        onFavoritesClick = onNavigateToFavorites,
                        onApplicationsClick = onNavigateToApplications
                    )
                    UserType.ORGANIZER -> OrganizerQuickActions(
                        onMyClubsClick = onNavigateToMyClubs,
                        onApplicationsClick = onNavigateToClubApplications
                    )
                    UserType.ADMIN -> AdminQuickActions(
                        onUsersClick = onNavigateToAdminUsers,
                        onClubsClick = onNavigateToAdminClubs,
                        onReviewsClick = onNavigateToAdminReviews
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка редактирования профиля
                OutlinedButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Редактировать профиль")
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Пользователь не авторизован",
                    color = TextSecondary
                )
            }
        }
    }

    // Диалог редактирования профиля
    if (showEditDialog) {
        currentUser?.let { user ->
            EditProfileDialog(
                user = user,
                onDismiss = { showEditDialog = false },
                onSave = { updatedUser ->
                    viewModel.updateUser(updatedUser)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
private fun ProfileHeader(user: User) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Аватар
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.fullName.firstOrNull()?.uppercase() ?: "?",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = user.fullName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Тип пользователя
        Surface(
            color = SecondaryColor,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = getUserTypeLabel(user.userType),
                fontSize = 14.sp,
                color = PrimaryColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ProfileInfoCard(user: User) {
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
                text = "Информация",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoRow(
                icon = Icons.Default.Phone,
                label = "Телефон",
                value = user.phone
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            ProfileInfoRow(
                icon = Icons.Default.LocationCity,
                label = "Город",
                value = user.city.ifEmpty { "Не указан" }
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            ProfileInfoRow(
                icon = Icons.Default.CalendarMonth,
                label = "Дата регистрации",
                value = formatDate(user.registrationDate)
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun ParentQuickActions(
    onChildrenClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onApplicationsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Быстрые действия",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ChildCare,
                title = "Дети",
                onClick = onChildrenClick
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Favorite,
                title = "Избранное",
                onClick = onFavoritesClick
            )
        }

        QuickActionCard(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Assignment,
            title = "Мои заявки",
            onClick = onApplicationsClick
        )
    }
}

@Composable
private fun ChildQuickActions(
    onFavoritesClick: () -> Unit,
    onApplicationsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Быстрые действия",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Favorite,
                title = "Избранное",
                onClick = onFavoritesClick
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Assignment,
                title = "Мои заявки",
                onClick = onApplicationsClick
            )
        }
    }
}

@Composable
private fun OrganizerQuickActions(
    onMyClubsClick: () -> Unit,
    onApplicationsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Управление",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Groups,
                title = "Мои кружки",
                onClick = onMyClubsClick
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ListAlt,
                title = "Заявки",
                onClick = onApplicationsClick
            )
        }
    }
}

@Composable
private fun AdminQuickActions(
    onUsersClick: () -> Unit,
    onClubsClick: () -> Unit,
    onReviewsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Администрирование",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.People,
                title = "Пользователи",
                onClick = onUsersClick
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Groups,
                title = "Кружки",
                onClick = onClubsClick
            )
        }

        QuickActionCard(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.RateReview,
            title = "Отзывы",
            onClick = onReviewsClick
        )
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    var fullName by remember { mutableStateOf(user.fullName) }
    var city by remember { mutableStateOf(user.city) }
    var phone by remember { mutableStateOf(user.phone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Редактирование профиля",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("ФИО") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Город") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(user.copy(
                        fullName = fullName,
                        city = city,
                        phone = phone
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = TextSecondary)
            }
        }
    )
}

private fun getUserTypeLabel(userType: UserType): String {
    return when (userType) {
        UserType.USER -> "Пользователь"
        UserType.CHILD -> "Ребёнок"
        UserType.ORGANIZER -> "Организатор"
        UserType.ADMIN -> "Администратор"
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

