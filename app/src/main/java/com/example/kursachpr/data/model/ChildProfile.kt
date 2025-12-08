package com.example.kursachpr.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "children",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChildProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: Long,              // ID родителя
    val name: String,                // Имя ребёнка
    val birthDate: Long,             // Дата рождения
    val age: Int,                    // Возраст
    val interests: String = "",      // Интересы (теги через запятую)
    val healthInfo: String = "",     // Особенности здоровья
    val additionalInfo: String = ""  // Дополнительная информация
)


