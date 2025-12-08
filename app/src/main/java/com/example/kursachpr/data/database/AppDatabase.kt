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

            // Создаём тестового родителя
            val parent = User(
                id = 3,
                userType = UserType.PARENT,
                fullName = "Петрова Мария Сергеевна",
                phone = "89007654321",
                password = "123456",
                city = "Муром"
            )
            userDao.insert(parent)

            // Создаём тестового ребёнка
            val child = User(
                id = 4,
                userType = UserType.CHILD,
                fullName = "Петров Алексей",
                phone = "89009876543",
                password = "123456",
                city = "Муром"
            )
            userDao.insert(child)

            // Создаём тестовые кружки (рейтинг 0 - будет рассчитан из отзывов)
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
                    rating = 0f,
                    reviewCount = 0
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
                    rating = 0f,
                    reviewCount = 0
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
                    rating = 0f,
                    reviewCount = 0
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
                    rating = 0f,
                    reviewCount = 0
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
                    rating = 0f,
                    reviewCount = 0
                )
            )
            
            clubs.forEach { clubDao.insert(it) }

            // Добавляем тестовые отзывы
            val reviewDao = database.reviewDao()
            val reviews = listOf(
                // Отзывы на "Лыжные гонки" (clubId = 1)
                Review(
                    clubId = 1,
                    userId = 3, // Родитель
                    rating = 5,
                    text = "Отличная секция! Сын занимается уже второй год, очень доволен. Тренеры внимательные и профессиональные.",
                    reply = "Спасибо за отзыв! Рады, что вашему сыну нравится!"
                ),
                Review(
                    clubId = 1,
                    userId = 4, // Ребёнок
                    rating = 5,
                    text = "Мне очень нравится! Тренировки интересные, уже участвовал в соревнованиях."
                ),
                Review(
                    clubId = 1,
                    userId = 3,
                    rating = 4,
                    text = "Хорошая организация, но хотелось бы больше занятий в неделю."
                ),
                
                // Отзывы на "Рисование" (clubId = 2)
                Review(
                    clubId = 2,
                    userId = 3,
                    rating = 5,
                    text = "Дочка в восторге! Преподаватель умеет заинтересовать детей. Уже нарисовала целую выставку домой.",
                    reply = "Благодарим за тёплые слова! Ждём вас на новых занятиях!"
                ),
                Review(
                    clubId = 2,
                    userId = 4,
                    rating = 4,
                    text = "Интересно учиться рисовать разными техниками."
                ),
                
                // Отзывы на "Робототехника" (clubId = 3)
                Review(
                    clubId = 3,
                    userId = 3,
                    rating = 5,
                    text = "Лучший кружок по робототехнике в городе! Ребёнок научился программировать и собирать роботов."
                ),
                Review(
                    clubId = 3,
                    userId = 4,
                    rating = 5,
                    text = "Супер! Мы даже участвовали в олимпиаде по робототехнике!"
                ),
                Review(
                    clubId = 3,
                    userId = 3,
                    rating = 4,
                    text = "Отличное оборудование, интересная программа. Немного дороговато, но оно того стоит."
                ),
                
                // Отзывы на "Шахматы" (clubId = 4)
                Review(
                    clubId = 4,
                    userId = 4,
                    rating = 5,
                    text = "Научился играть с нуля! Теперь обыгрываю папу 😄"
                ),
                Review(
                    clubId = 4,
                    userId = 3,
                    rating = 4,
                    text = "Хороший преподаватель, терпеливый. Сын стал более усидчивым."
                ),
                
                // Отзывы на "Современные танцы" (clubId = 5)
                Review(
                    clubId = 5,
                    userId = 4,
                    rating = 5,
                    text = "Обожаю эти танцы! Хореограф очень крутой, учит современным движениям."
                ),
                Review(
                    clubId = 5,
                    userId = 3,
                    rating = 4,
                    text = "Дочь ходит с удовольствием. Есть выступления на городских мероприятиях."
                ),
                Review(
                    clubId = 5,
                    userId = 4,
                    rating = 5,
                    text = "Лучший танцевальный кружок! Атмосфера дружная, много концертов."
                )
            )
            
            reviews.forEach { reviewDao.insert(it) }
            
            // Обновляем рейтинги кружков на основе отзывов
            for (clubId in 1L..5L) {
                val avgRating = reviewDao.getAverageRating(clubId) ?: 0f
                val reviewCount = reviewDao.getReviewCount(clubId)
                clubDao.updateRating(clubId, avgRating, reviewCount)
            }
        }
    }
}


