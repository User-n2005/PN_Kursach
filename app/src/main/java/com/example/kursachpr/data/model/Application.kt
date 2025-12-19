package com.example.kursachpr.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val clubId: Long,
    val userId: Long,
    val childId: Long? = null,
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
