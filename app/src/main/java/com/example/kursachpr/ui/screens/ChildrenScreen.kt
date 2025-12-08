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
import com.example.kursachpr.data.model.ChildProfile
import com.example.kursachpr.ui.components.TopBar
import com.example.kursachpr.ui.theme.*
import com.example.kursachpr.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChildrenScreen(
    viewModel: MainViewModel,
    onMenuClick: () -> Unit
) {
    val children by viewModel.getChildren().collectAsState(initial = emptyList())
    val currentUser by viewModel.currentUser.collectAsState()
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingChild by remember { mutableStateOf<ChildProfile?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        TopBar(
            title = "Анкеты детей",
            onMenuClick = onMenuClick
        )

        if (children.isEmpty()) {
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
                        imageVector = Icons.Default.ChildCare,
                        contentDescription = null,
                        tint = SecondaryColor,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Нет добавленных детей",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Добавьте анкету ребёнка для\nперсонализированного поиска кружков",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Добавить ребёнка")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(children, key = { it.id }) { child ->
                    ChildCard(
                        child = child,
                        onEdit = { editingChild = child },
                        onDelete = {
                            scope.launch {
                                viewModel.deleteChild(child)
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Добавить ребёнка")
                    }
                }
            }
        }
    }

    // Диалог добавления ребёнка
    if (showAddDialog) {
        currentUser?.let { user ->
            AddEditChildDialog(
                parentId = user.id,
                child = null,
                onDismiss = { showAddDialog = false },
                onSave = { newChild ->
                    scope.launch {
                        viewModel.addChild(newChild)
                        showAddDialog = false
                    }
                }
            )
        }
    }

    // Диалог редактирования ребёнка
    editingChild?.let { child ->
        currentUser?.let { user ->
            AddEditChildDialog(
                parentId = user.id,
                child = child,
                onDismiss = { editingChild = null },
                onSave = { updatedChild ->
                    scope.launch {
                        viewModel.updateChild(updatedChild)
                        editingChild = null
                    }
                }
            )
        }
    }
}

@Composable
private fun ChildCard(
    child: ChildProfile,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(SecondaryColor, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = PrimaryColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = child.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${child.age} лет",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Редактировать",
                            tint = PrimaryColor
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = Color.Red.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (child.interests.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Интересы",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = child.interests,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }

            if (child.additionalInfo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Дополнительно",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = child.additionalInfo,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить анкету?") },
            text = { Text("Вы уверены, что хотите удалить анкету ${child.name}?") },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditChildDialog(
    parentId: Long,
    child: ChildProfile?,
    onDismiss: () -> Unit,
    onSave: (ChildProfile) -> Unit
) {
    var name by remember { mutableStateOf(child?.name ?: "") }
    var age by remember { mutableStateOf(child?.age?.toString() ?: "") }
    var interests by remember { mutableStateOf(child?.interests ?: "") }
    var healthInfo by remember { mutableStateOf(child?.healthInfo ?: "") }
    var additionalInfo by remember { mutableStateOf(child?.additionalInfo ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (child == null) "Добавить ребёнка" else "Редактировать анкету",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя ребёнка") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.all { c -> c.isDigit() }) age = it },
                    label = { Text("Возраст") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = interests,
                    onValueChange = { interests = it },
                    label = { Text("Интересы (через запятую)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                OutlinedTextField(
                    value = healthInfo,
                    onValueChange = { healthInfo = it },
                    label = { Text("Особенности здоровья") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                OutlinedTextField(
                    value = additionalInfo,
                    onValueChange = { additionalInfo = it },
                    label = { Text("Дополнительная информация") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ageInt = age.toIntOrNull() ?: 0
                    val birthDate = System.currentTimeMillis() - (ageInt * 365L * 24 * 60 * 60 * 1000)
                    
                    onSave(
                        ChildProfile(
                            id = child?.id ?: 0,
                            parentId = parentId,
                            name = name,
                            birthDate = birthDate,
                            age = ageInt,
                            interests = interests,
                            healthInfo = healthInfo,
                            additionalInfo = additionalInfo
                        )
                    )
                },
                enabled = name.isNotBlank() && age.isNotBlank(),
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

