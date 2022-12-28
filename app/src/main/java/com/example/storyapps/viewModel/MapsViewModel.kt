package com.example.storyapps.viewModel

import androidx.lifecycle.ViewModel
import com.example.storyapps.data.Repository

class MapsViewModel constructor(private val repository: Repository) : ViewModel() {

    fun getAllStoriesWithLocation(token: String) = repository.getAllStoryWithLocation(token)

}