package com.example.kursachpr.data.dao

import androidx.room.*
import com.example.kursachpr.data.model.Club
import com.example.kursachpr.data.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesByUser(userId: Long): Flow<List<Favorite>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND clubId = :clubId)")
    suspend fun isFavorite(userId: Long, clubId: Long): Boolean

    @Query("SELECT c.* FROM clubs c INNER JOIN favorites f ON c.id = f.clubId WHERE f.userId = :userId")
    fun getFavoriteClubs(userId: Long): Flow<List<Club>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite): Long

    @Query("DELETE FROM favorites WHERE userId = :userId AND clubId = :clubId")
    suspend fun delete(userId: Long, clubId: Long)
}


