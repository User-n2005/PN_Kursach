package com.example.kursachpr.data.dao

import androidx.room.*
import com.example.kursachpr.data.model.Club
import com.example.kursachpr.data.model.ClubCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ClubDao {
    @Query("SELECT * FROM clubs WHERE isActive = 1 ORDER BY rating DESC")
    fun getAllClubs(): Flow<List<Club>>

    @Query("SELECT * FROM clubs WHERE isActive = 1 ORDER BY rating DESC LIMIT 3")
    fun getTopClubs(): Flow<List<Club>>

    @Query("SELECT * FROM clubs WHERE id = :id")
    suspend fun getClubById(id: Long): Club?

    @Query("SELECT * FROM clubs WHERE organizerId = :organizerId")
    fun getClubsByOrganizer(organizerId: Long): Flow<List<Club>>

    @Query("""
        SELECT * FROM clubs 
        WHERE isActive = 1 
        AND (:city = '' OR city LIKE '%' || :city || '%')
        AND (:category IS NULL OR category = :category)
        AND (:minAge IS NULL OR ageFrom <= :minAge)
        AND (:maxAge IS NULL OR ageTo >= :maxAge)
        AND (:maxPrice IS NULL OR pricePerMonth <= :maxPrice)
        ORDER BY rating DESC
    """)
    fun searchClubs(
        city: String = "",
        category: ClubCategory? = null,
        minAge: Int? = null,
        maxAge: Int? = null,
        maxPrice: Int? = null
    ): Flow<List<Club>>

    @Query("SELECT * FROM clubs WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchByQuery(query: String): Flow<List<Club>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(club: Club): Long

    @Update
    suspend fun update(club: Club)

    @Delete
    suspend fun delete(club: Club)

    @Query("DELETE FROM clubs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE clubs SET isVerified = :isVerified WHERE id = :id")
    suspend fun setVerified(id: Long, isVerified: Boolean)

    @Query("UPDATE clubs SET rating = :rating, reviewCount = :reviewCount WHERE id = :id")
    suspend fun updateRating(id: Long, rating: Float, reviewCount: Int)
}


