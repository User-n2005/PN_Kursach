package com.example.kursachpr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserType {
    USER,
    CHILD,
    ORGANIZER,
    ADMIN
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userType: UserType,
    val fullName: String,
    val phone: String,
    val password: String,
    val city: String = "",
    val registrationDate: Long = System.currentTimeMillis()
)
