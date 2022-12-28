package com.example.storyapps.viewModel

import androidx.lifecycle.ViewModel
import com.example.storyapps.data.Repository
import com.example.storyapps.data.datalocal.UploadResponse
import com.example.storyapps.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel constructor(private val repository: Repository) : ViewModel() {

    fun getAuthToken(): Flow<String?> = repository.getToken()

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Resource<UploadResponse>> = repository.uploadStory(token, file, description,lat,lon)


}