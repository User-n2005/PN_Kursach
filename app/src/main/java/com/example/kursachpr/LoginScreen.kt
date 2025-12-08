package com.example.kursachpr

import com.example.kursachpr.ui.components.FoxToast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch

// Данные для автозаполнения логинов
data class LoginSuggestion(
    val login: String,
    val label: String
)

private val loginSuggestions = listOf(
    LoginSuggestion("admin", "Администратор"),
    LoginSuggestion("89001234567", "Организатор"),
    LoginSuggestion("89007654321", "Пользователь"),
    LoginSuggestion("89009876543", "Ребёнок")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    
    // Фильтруем подсказки по введённому тексту
    val filteredSuggestions = remember(phone) {
        if (phone.isEmpty()) {
            loginSuggestions
        } else {
            loginSuggestions.filter { 
                it.login.contains(phone, ignoreCase = true) ||
                it.label.contains(phone, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Логотип
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.fox_logo),
                contentDescription = "Логотип",
                modifier = Modifier.size(140.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Добро пожаловать!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Text(
            text = "Войдите в свой аккаунт",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Поле телефона/логина с автозаполнением
        ExposedDropdownMenuBox(
            expanded = expanded && filteredSuggestions.isNotEmpty(),
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = phone,
                onValueChange = { 
                    phone = it
                    expanded = true
                },
                label = { Text("Телефон или логин") },
                placeholder = { Text("Введите телефон или логин") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = SecondaryColor,
                    focusedLabelColor = AccentColor,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = AccentColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            ExposedDropdownMenu(
                expanded = expanded && filteredSuggestions.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                filteredSuggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = suggestion.login,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = suggestion.label,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        },
                        onClick = {
                            phone = suggestion.login
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Поле пароля
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showPassword) "Скрыть пароль" else "Показать пароль",
                        tint = Color.Gray
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentColor,
                unfocusedBorderColor = SecondaryColor,
                focusedLabelColor = AccentColor,
                unfocusedLabelColor = Color.Gray,
                cursorColor = AccentColor,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Кнопка входа
        Button(
            onClick = {
                if (phone.isBlank() || password.isBlank()) {
                    FoxToast.show(context, "Заполните все поля")
                    return@Button
                }
                
                isLoading = true
                scope.launch {
                    val user = viewModel.login(phone, password)
                    isLoading = false
                    
                    if (user != null) {
                        FoxToast.show(context, "Добро пожаловать, ${user.fullName}!")
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        FoxToast.show(context, "Неверный логин или пароль")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                contentColor = Color.White
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Войти",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ссылка на регистрацию
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Нет аккаунта?",
                fontSize = 14.sp,
                color = Color.Gray
            )
            TextButton(
                onClick = { 
                    navController.navigate("registration") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            ) {
                Text(
                    text = "Зарегистрироваться",
                    fontSize = 14.sp,
                    color = AccentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
