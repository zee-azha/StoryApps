package com.example.storyapps.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoriesDao {
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insertStory(vararg stories: StoriesResponseItem)

    @Query("SELECT * FROM story")
    fun getStories(): PagingSource<Int, StoriesResponseItem>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}