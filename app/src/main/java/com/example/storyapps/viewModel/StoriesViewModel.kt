package com.example.storyapps.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapps.data.Repository
import com.example.storyapps.data.database.StoriesResponseItem

class StoriesViewModel  constructor(private val repository: Repository) : ViewModel() {

    fun getStory(token: String): LiveData<PagingData<StoriesResponseItem>> =
        repository.getAllStories(token).cachedIn(viewModelScope).asLiveData()
}
