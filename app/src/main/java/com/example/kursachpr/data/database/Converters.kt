package com.example.kursachpr.data.database

import androidx.room.TypeConverter
import com.example.kursachpr.data.model.ApplicationStatus
import com.example.kursachpr.data.model.ClubCategory
import com.example.kursachpr.data.model.UserType

class Converters {
    @TypeConverter
    fun fromUserType(value: UserType): String = value.name

    @TypeConverter
    fun toUserType(value: String): UserType = UserType.valueOf(value)

    @TypeConverter
    fun fromClubCategory(value: ClubCategory): String = value.name

    @TypeConverter
    fun toClubCategory(value: String): ClubCategory = ClubCategory.valueOf(value)

    @TypeConverter
    fun fromApplicationStatus(value: ApplicationStatus): String = value.name

    @TypeConverter
    fun toApplicationStatus(value: String): ApplicationStatus = ApplicationStatus.valueOf(value)
}


