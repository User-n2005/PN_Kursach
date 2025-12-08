package com.example.kursachpr.viewmodel

import android.app.Application as AndroidApplication
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursachpr.data.database.AppDatabase
import com.example.kursachpr.data.model.*
import com.example.kursachpr.data.model.Application as ClubApplication
import com.example.kursachpr.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: AndroidApplication) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(
        database.userDao(),
        database.clubDao(),
        database.childDao(),
        database.reviewDao(),
        database.applicationDao(),
        database.favoriteDao()
    )

    // Текущий пользователь
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Топ-3 кружка
    val topClubs: StateFlow<List<Club>> = repository.getTopClubs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Все кружки
    val allClubs: StateFlow<List<Club>> = repository.getAllClubs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Результаты поиска
    private val _searchResults = MutableStateFlow<List<Club>>(emptyList())
    val searchResults: StateFlow<List<Club>> = _searchResults.asStateFlow()

    // Фильтры поиска
    private val _searchCity = MutableStateFlow("")
    private val _searchCategory = MutableStateFlow<ClubCategory?>(null)
    private val _searchAge = MutableStateFlow<Int?>(null)
    private val _searchMaxPrice = MutableStateFlow<Int?>(null)

    // === Авторизация ===
    suspend fun login(phone: String, password: String): User? {
        val user = repository.login(phone, password)
        if (user != null) {
            _currentUser.value = user
        }
        return user
    }

    suspend fun register(user: User): Long {
        return repository.insertUser(user)
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun isPhoneExists(phone: String): Boolean {
        return repository.getUserByPhone(phone) != null
    }

    // === Поиск кружков ===
    fun searchClubs(
        city: String = "",
        category: ClubCategory? = null,
        age: Int? = null,
        maxPrice: Int? = null
    ) {
        viewModelScope.launch {
            repository.searchClubs(city, category, age, age, maxPrice)
                .collect { clubs ->
                    _searchResults.value = clubs
                }
        }
    }

    fun searchByQuery(query: String) {
        viewModelScope.launch {
            repository.searchByQuery(query)
                .collect { clubs ->
                    _searchResults.value = clubs
                }
        }
    }

    // === Кружки ===
    suspend fun getClubById(id: Long): Club? = repository.getClubById(id)

    fun getClubsByOrganizer(organizerId: Long): Flow<List<Club>> = 
        repository.getClubsByOrganizer(organizerId)

    suspend fun insertClub(club: Club): Long = repository.insertClub(club)
    
    suspend fun updateClub(club: Club) = repository.updateClub(club)
    
    suspend fun deleteClub(id: Long) = repository.deleteClub(id)
    
    suspend fun setClubVerified(id: Long, isVerified: Boolean) = 
        repository.setClubVerified(id, isVerified)

    // === Избранное ===
    fun getFavorites(): Flow<List<Favorite>> {
        val userId = _currentUser.value?.id ?: return flowOf(emptyList())
        return repository.getFavoritesByUser(userId)
    }

    suspend fun isFavorite(clubId: Long): Boolean {
        val userId = _currentUser.value?.id ?: return false
        return repository.isFavorite(userId, clubId)
    }

    suspend fun toggleFavorite(clubId: Long) {
        val userId = _currentUser.value?.id ?: return
        if (repository.isFavorite(userId, clubId)) {
            repository.removeFavorite(userId, clubId)
        } else {
            repository.addFavorite(Favorite(userId = userId, clubId = clubId))
        }
    }

    // === Заявки ===
    fun getMyApplications(): Flow<List<ClubApplication>> {
        val userId = _currentUser.value?.id ?: return flowOf(emptyList())
        return repository.getApplicationsByUser(userId)
    }

    fun getApplicationsForClub(clubId: Long): Flow<List<ClubApplication>> =
        repository.getApplicationsByClub(clubId)

    suspend fun submitApplication(clubId: Long, childId: Long? = null, message: String = "") {
        val userId = _currentUser.value?.id ?: return
        repository.insertApplication(
            ClubApplication(
                clubId = clubId,
                userId = userId,
                childId = childId,
                message = message
            )
        )
    }

    suspend fun updateApplicationStatus(id: Long, status: ApplicationStatus) =
        repository.updateApplicationStatus(id, status)

    // === Отзывы ===
    fun getReviewsForClub(clubId: Long): Flow<List<Review>> =
        repository.getReviewsByClub(clubId)

    fun getAllReviews(): Flow<List<Review>> = repository.getAllReviews()

    suspend fun addReview(clubId: Long, rating: Int, text: String) {
        val userId = _currentUser.value?.id ?: return
        repository.insertReview(
            Review(
                clubId = clubId,
                userId = userId,
                rating = rating,
                text = text
            )
        )
    }

    suspend fun deleteReview(id: Long) = repository.deleteReview(id)

    suspend fun replyToReview(id: Long, reply: String) = repository.addReplyToReview(id, reply)

    // === Дети (анкеты) ===
    fun getChildren(): Flow<List<ChildProfile>> {
        val userId = _currentUser.value?.id ?: return flowOf(emptyList())
        return repository.getChildrenByParent(userId)
    }

    suspend fun addChild(child: ChildProfile): Long = repository.insertChild(child)

    suspend fun updateChild(child: ChildProfile) = repository.updateChild(child)

    suspend fun deleteChild(child: ChildProfile) = repository.deleteChild(child)

    // === Пользователи (для админа) ===
    fun getAllUsers(): Flow<List<User>> = repository.getAllUsers()

    suspend fun deleteUser(id: Long) = repository.deleteUser(id)
}

