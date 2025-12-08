package com.example.kursachpr.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kursachpr.data.dao.*
import com.example.kursachpr.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Club::class,
        ChildProfile::class,
        Review::class,
        Application::class,
        Favorite::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun clubDao(): ClubDao
    abstract fun childDao(): ChildDao
    abstract fun reviewDao(): ReviewDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "club_aggregator_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Callback для добавления начальных данных
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val userDao = database.userDao()
            val clubDao = database.clubDao()

            // Создаём аккаунт администратора
            val admin = User(
                id = 1,
                userType = UserType.ADMIN,
                fullName = "Администратор",
                phone = "admin",
                password = "admin123",
                city = "Муром"
            )
            userDao.insert(admin)

            // Создаём тестового организатора
            val organizer = User(
                id = 2,
                userType = UserType.ORGANIZER,
                fullName = "Иванов Иван Иванович",
                phone = "89001234567",
                password = "123456",
                city = "Муром"
            )
            userDao.insert(organizer)

            // Создаём тестовые кружки
            val clubs = listOf(
                Club(
                    id = 1,
                    organizerId = 2,
                    name = "Лыжные гонки",
                    description = "Секция по лыжным гонкам для детей и подростков. Профессиональные тренеры, современное оборудование. Занятия проходят на свежем воздухе.",
                    category = ClubCategory.SPORT,
                    city = "Муром",
                    district = "Центральный",
                    address = "ул. Ленина, 15",
                    ageFrom = 6,
                    ageTo = 18,
                    pricePerMonth = 2000,
                    schedule = "Пн, Ср, Пт 16:00-18:00",
                    isVerified = true,
                    rating = 4.8f,
                    reviewCount = 15
                ),
                Club(
                    id = 2,
                    organizerId = 2,
                    name = "Рисование",
                    description = "Студия рисования для детей. Акварель, гуашь, карандаши. Развиваем творческие способности и воображение.",
                    category = ClubCategory.ART,
                    city = "Муром",
                    district = "Южный",
                    address = "ул. Пушкина, 25",
                    ageFrom = 4,
                    ageTo = 14,
                    pricePerMonth = 1500,
                    schedule = "Вт, Чт 15:00-17:00",
                    isVerified = true,
                    rating = 4.5f,
                    reviewCount = 8
                ),
                Club(
                    id = 3,
                    organizerId = 2,
                    name = "Робототехника",
                    description = "Кружок робототехники и программирования. Lego, Arduino, Python. Участие в соревнованиях.",
                    category = ClubCategory.IT,
                    city = "Муром",
                    district = "Центральный",
                    address = "ул. Московская, 10",
                    ageFrom = 8,
                    ageTo = 16,
                    pricePerMonth = 3000,
                    schedule = "Сб 10:00-13:00",
                    isVerified = false,
                    rating = 4.9f,
                    reviewCount = 22
                ),
                Club(
                    id = 4,
                    organizerId = 2,
                    name = "Шахматы",
                    description = "Шахматная школа для начинающих и продвинутых. Участие в турнирах, разряды.",
                    category = ClubCategory.SCIENCE,
                    city = "Муром",
                    district = "Северный",
                    address = "ул. Гагарина, 5",
                    ageFrom = 5,
                    ageTo = 99,
                    pricePerMonth = 1000,
                    schedule = "Пн, Ср 17:00-19:00",
                    isVerified = true,
                    rating = 4.7f,
                    reviewCount = 12
                ),
                Club(
                    id = 5,
                    organizerId = 2,
                    name = "Современные танцы",
                    description = "Hip-hop, breaking, contemporary. Для тех, кто хочет научиться танцевать современные стили.",
                    category = ClubCategory.DANCE,
                    city = "Муром",
                    district = "Центральный",
                    address = "ул. Карла Маркса, 30",
                    ageFrom = 7,
                    ageTo = 20,
                    pricePerMonth = 2500,
                    schedule = "Вт, Чт, Сб 18:00-20:00",
                    isVerified = true,
                    rating = 4.6f,
                    reviewCount = 18
                )
            )
            
            clubs.forEach { clubDao.insert(it) }
        }
    }
}


