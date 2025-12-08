package com.example.kursachpr.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Статус заявки
enum class ApplicationStatus(val title: String) {
    PENDING("Ожидание"),
    APPROVED("Подтверждена"),
    REJECTED("Отклонена"),
    CANCELLED("Отменена")
}

@Entity(
    tableName = "applications",
    foreignKeys = [
        ForeignKey(
            entity = Club::class,
            parentColumns = ["id"],
            childColumns = ["clubId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Application(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clubId: Long,                        // ID кружка
    val userId: Long,                        // ID пользователя
    val childId: Long? = null,               // ID ребёнка (если заявка от родителя)
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val message: String = "",                // Сообщение к заявке
    val createdAt: Long = System.currentTimeMillis()
)


