package com.example.kursachpr.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Направления кружков
enum class ClubCategory(val title: String) {
    SPORT("Спорт"),
    ART("Творчество"),
    SCIENCE("Наука"),
    MUSIC("Музыка"),
    DANCE("Танцы"),
    LANGUAGE("Языки"),
    IT("IT и программирование"),
    OTHER("Другое")
}

@Entity(
    tableName = "clubs",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["organizerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Club(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val organizerId: Long,           // ID организатора
    val name: String,                // Название кружка
    val description: String,         // Описание
    val category: ClubCategory,      // Направление
    val city: String,                // Город
    val district: String = "",       // Район
    val address: String,             // Адрес
    val ageFrom: Int,                // Возраст от
    val ageTo: Int,                  // Возраст до
    val pricePerMonth: Int,          // Стоимость в месяц
    val schedule: String,            // Расписание занятий
    val imageUrl: String = "",       // Фото (путь к изображению)
    val isVerified: Boolean = false, // Верифицирован ли кружок
    val isActive: Boolean = true,    // Активен ли кружок
    val rating: Float = 0f,          // Рейтинг (1-5)
    val reviewCount: Int = 0,        // Количество отзывов
    val createdAt: Long = System.currentTimeMillis()
)


