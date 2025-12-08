package com.example.kursachpr.data.dao

import androidx.room.*
import com.example.kursachpr.data.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesByUser(userId: Long): Flow<List<Favorite>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND clubId = :clubId)")
    suspend fun isFavorite(userId: Long, clubId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite): Long

    @Query("DELETE FROM favorites WHERE userId = :userId AND clubId = :clubId")
    suspend fun delete(userId: Long, clubId: Long)
}


