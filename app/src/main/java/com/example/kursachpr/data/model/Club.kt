package com.example.kursachpr.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val organizerId: Long,
    val name: String,
    val description: String,
    val category: ClubCategory,
    val city: String,
    val district: String = "",
    val address: String,
    val ageFrom: Int,
    val ageTo: Int,
    val pricePerMonth: Int,
    val schedule: String,
    val imageUrl: String = "",
    val isVerified: Boolean = false,
    val isActive: Boolean = true,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
