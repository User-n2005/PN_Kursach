package com.example.kursachpr.data.repository

import com.example.kursachpr.data.dao.*
import com.example.kursachpr.data.model.*
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val clubDao: ClubDao,
    private val childDao: ChildDao,
    private val reviewDao: ReviewDao,
    private val applicationDao: ApplicationDao,
    private val favoriteDao: FavoriteDao
) {
    // === Users ===
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    suspend fun getUserById(id: Long): User? = userDao.getUserById(id)
    suspend fun login(phone: String, password: String): User? = userDao.login(phone, password)
    suspend fun getUserByPhone(phone: String): User? = userDao.getUserByPhone(phone)
    suspend fun insertUser(user: User): Long = userDao.insert(user)
    suspend fun updateUser(user: User) = userDao.update(user)
    suspend fun deleteUser(id: Long) = userDao.deleteById(id)

    // === Clubs ===
    fun getAllClubs(): Flow<List<Club>> = clubDao.getAllClubs()
    fun getTopClubs(): Flow<List<Club>> = clubDao.getTopClubs()
    suspend fun getClubById(id: Long): Club? = clubDao.getClubById(id)
    fun getClubsByOrganizer(organizerId: Long): Flow<List<Club>> = clubDao.getClubsByOrganizer(organizerId)
    fun searchClubs(
        city: String = "",
        category: ClubCategory? = null,
        minAge: Int? = null,
        maxAge: Int? = null,
        maxPrice: Int? = null
    ): Flow<List<Club>> = clubDao.searchClubs(city, category, minAge, maxAge, maxPrice)
    fun searchByQuery(query: String): Flow<List<Club>> = clubDao.searchByQuery(query)
    suspend fun insertClub(club: Club): Long = clubDao.insert(club)
    suspend fun updateClub(club: Club) = clubDao.update(club)
    suspend fun deleteClub(id: Long) = clubDao.deleteById(id)
    suspend fun setClubVerified(id: Long, isVerified: Boolean) = clubDao.setVerified(id, isVerified)

    // === Children ===
    fun getChildrenByParent(parentId: Long): Flow<List<ChildProfile>> = childDao.getChildrenByParent(parentId)
    suspend fun getChildById(id: Long): ChildProfile? = childDao.getChildById(id)
    suspend fun insertChild(child: ChildProfile): Long = childDao.insert(child)
    suspend fun updateChild(child: ChildProfile) = childDao.update(child)
    suspend fun deleteChild(child: ChildProfile) = childDao.delete(child)

    // === Reviews ===
    fun getReviewsByClub(clubId: Long): Flow<List<Review>> = reviewDao.getReviewsByClub(clubId)
    fun getAllReviews(): Flow<List<Review>> = reviewDao.getAllReviews()
    suspend fun insertReview(review: Review): Long {
        val id = reviewDao.insert(review)
        updateClubRating(review.clubId)
        return id
    }
    suspend fun deleteReview(id: Long, clubId: Long) {
        reviewDao.deleteById(id)
        updateClubRating(clubId)
    }
    suspend fun addReplyToReview(id: Long, reply: String) = reviewDao.addReply(id, reply)
    
    suspend fun getAverageRating(clubId: Long): Float = reviewDao.getAverageRating(clubId) ?: 0f
    suspend fun getReviewCount(clubId: Long): Int = reviewDao.getReviewCount(clubId)
    
    suspend fun updateClubRating(clubId: Long) {
        val avgRating = reviewDao.getAverageRating(clubId) ?: 0f
        val reviewCount = reviewDao.getReviewCount(clubId)
        clubDao.updateRating(clubId, avgRating, reviewCount)
    }

    // === Applications ===
    fun getApplicationsByUser(userId: Long): Flow<List<Application>> = applicationDao.getApplicationsByUser(userId)
    fun getApplicationsByClub(clubId: Long): Flow<List<Application>> = applicationDao.getApplicationsByClub(clubId)
    suspend fun insertApplication(application: Application): Long = applicationDao.insert(application)
    suspend fun updateApplicationStatus(id: Long, status: ApplicationStatus) = applicationDao.updateStatus(id, status)

    // === Favorites ===
    fun getFavoritesByUser(userId: Long): Flow<List<Favorite>> = favoriteDao.getFavoritesByUser(userId)
    fun getFavoriteClubs(userId: Long): Flow<List<Club>> = favoriteDao.getFavoriteClubs(userId)
    suspend fun isFavorite(userId: Long, clubId: Long): Boolean = favoriteDao.isFavorite(userId, clubId)
    suspend fun addFavorite(favorite: Favorite): Long = favoriteDao.insert(favorite)
    suspend fun removeFavorite(userId: Long, clubId: Long) = favoriteDao.delete(userId, clubId)

    // === Applications for Organizer ===
    fun getApplicationsForOrganizer(organizerId: Long): Flow<List<Application>> = 
        applicationDao.getApplicationsForOrganizer(organizerId)
}


