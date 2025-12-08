package com.example.kursachpr.data.dao

import androidx.room.*
import com.example.kursachpr.data.model.ChildProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {
    @Query("SELECT * FROM children WHERE parentId = :parentId")
    fun getChildrenByParent(parentId: Long): Flow<List<ChildProfile>>

    @Query("SELECT * FROM children WHERE id = :id")
    suspend fun getChildById(id: Long): ChildProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(child: ChildProfile): Long

    @Update
    suspend fun update(child: ChildProfile)

    @Delete
    suspend fun delete(child: ChildProfile)
}


