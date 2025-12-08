package com.example.kursachpr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursachpr.data.model.UserType
import com.example.kursachpr.ui.theme.PrimaryColor

data class DrawerMenuItem(
    val title: String,
    val route: String
)

@Composable
fun DrawerMenu(
    userType: UserType?,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = when (userType) {
        UserType.USER -> listOf(
            DrawerMenuItem("Главная", "home"),
            DrawerMenuItem("Личный кабинет", "profile"),
            DrawerMenuItem("Мои записи", "my_applications"),
            DrawerMenuItem("Избранное", "favorites"),
            DrawerMenuItem("Дети", "children")
        )
        UserType.CHILD -> listOf(
            DrawerMenuItem("Главная", "home"),
            DrawerMenuItem("Личный кабинет", "profile"),
            DrawerMenuItem("Мои записи", "my_applications"),
            DrawerMenuItem("Избранное", "favorites")
        )
        UserType.ORGANIZER -> listOf(
            DrawerMenuItem("Главная", "home"),
            DrawerMenuItem("Личный кабинет", "profile"),
            DrawerMenuItem("Мои кружки", "my_clubs"),
            DrawerMenuItem("Заявки", "club_applications")
        )
        UserType.ADMIN -> listOf(
            DrawerMenuItem("Главная", "home"),
            DrawerMenuItem("Личный кабинет", "profile"),
            DrawerMenuItem("Пользователи", "admin_users"),
            DrawerMenuItem("Кружки", "admin_clubs"),
            DrawerMenuItem("Отзывы", "admin_reviews")
        )
        null -> listOf(
            DrawerMenuItem("Главная", "home")
        )
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(PrimaryColor.copy(alpha = 0.93f))
            .padding(top = 48.dp)
    ) {
        menuItems.forEach { item ->
            Text(
                text = item.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item.route) }
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка выхода
        Text(
            text = "Выйти",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(bottom = 32.dp)
        )
    }
}


