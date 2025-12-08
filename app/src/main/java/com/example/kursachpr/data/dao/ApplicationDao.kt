package com.example.kursachpr.data.dao

import androidx.room.*
import com.example.kursachpr.data.model.Application
import com.example.kursachpr.data.model.ApplicationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getApplicationsByUser(userId: Long): Flow<List<Application>>

    @Query("SELECT * FROM applications WHERE clubId = :clubId ORDER BY createdAt DESC")
    fun getApplicationsByClub(clubId: Long): Flow<List<Application>>

    @Query("SELECT * FROM applications WHERE id = :id")
    suspend fun getApplicationById(id: Long): Application?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(application: Application): Long

    @Update
    suspend fun update(application: Application)

    @Query("UPDATE applications SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: ApplicationStatus)

    @Delete
    suspend fun delete(application: Application)
}


