package com.example.kursachpr.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
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
data class Review(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clubId: Long,               // ID кружка
    val userId: Long,               // ID пользователя (родитель или ребёнок)
    val rating: Int,                // Рейтинг (1-5)
    val text: String,               // Текст отзыва
    val reply: String = "",         // Ответ организатора
    val isApproved: Boolean = true, // Одобрен ли отзыв
    val createdAt: Long = System.currentTimeMillis()
)


