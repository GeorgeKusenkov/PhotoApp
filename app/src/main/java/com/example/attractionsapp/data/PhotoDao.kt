package com.example.attractionsapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.attractionsapp.entity.NewPhoto
import com.example.attractionsapp.entity.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photo")
    fun getAll(): Flow<List<Photo>>

    @Insert(entity = Photo::class)
    suspend fun insert(photo: NewPhoto)

    @Update
    suspend fun update(photo: Photo)
}