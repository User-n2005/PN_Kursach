package com.example.kursachpr.data.dao

import androidx.room.*
import com.example.kursachpr.data.model.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE clubId = :clubId AND isApproved = 1 ORDER BY createdAt DESC")
    fun getReviewsByClub(clubId: Long): Flow<List<Review>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE id = :id")
    suspend fun getReviewById(id: Long): Review?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: Review): Long

    @Update
    suspend fun update(review: Review)

    @Delete
    suspend fun delete(review: Review)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE reviews SET reply = :reply WHERE id = :id")
    suspend fun addReply(id: Long, reply: String)

    @Query("SELECT AVG(CAST(rating AS FLOAT)) FROM reviews WHERE clubId = :clubId AND isApproved = 1")
    suspend fun getAverageRating(clubId: Long): Float?

    @Query("SELECT COUNT(*) FROM reviews WHERE clubId = :clubId AND isApproved = 1")
    suspend fun getReviewCount(clubId: Long): Int
}


