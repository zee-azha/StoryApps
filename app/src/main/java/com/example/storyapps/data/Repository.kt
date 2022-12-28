package com.example.storyapps.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapps.data.database.StoriesDatabase
import com.example.storyapps.data.database.StoriesResponseItem
import com.example.storyapps.data.datalocal.LoginResponse
import com.example.storyapps.data.datalocal.RegisterResponse
import com.example.storyapps.data.datalocal.StoriesResponse
import com.example.storyapps.data.datalocal.UploadResponse
import com.example.storyapps.data.remote.Api
import com.example.storyapps.data.remote.StoryRemoteMediator
import com.example.storyapps.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody


class Repository constructor(
    private val apiService: Api,
    private val saveAuthPreferences: SaveAuthPreferences,
    private val database: StoriesDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStories(token: String): Flow<PagingData<StoriesResponseItem>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(
                database,
                apiService,
                generateToken(token)
            ),
            pagingSourceFactory = {
                database.storiesDao().getStories()
            }
        ).flow
    }


    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody?= null
    ): Flow<Resource<UploadResponse>> = flow {
        try {
            val tokenAuth = generateToken(token)
            val response = apiService.uploadStory(tokenAuth, file, description,lat,lon)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Failure(e.toString()))

        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<RegisterResponse>> = flow {
        try {
            val response = apiService.userRegister(name, email, password)
            emit(Resource.Success(response))
        } catch (e: Exception) {

            e.printStackTrace()
            emit(Resource.Failure(e.message.toString()))

        }
    }.flowOn(Dispatchers.IO)

    suspend fun login(
        email: String,
        password: String
    ): Flow<Resource<LoginResponse>> = flow {
        try {
            val response = apiService.userLogin(email, password)
            emit(Resource.Success(response))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Failure(e.message.toString()))
        }

    }.flowOn(Dispatchers.IO)

    fun getAllStoryWithLocation(token: String): Flow<Resource<StoriesResponse>> = flow{

        try {
            val bearerToken = generateToken(token)
            val response = apiService.getAllStories(bearerToken, size = 30, location = 1)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Failure(e.message.toString()))
        }
    }

    suspend fun saveToken(token: String) {
        saveAuthPreferences.saveAuthToken(token)
    }

    fun getToken(): Flow<String?> = saveAuthPreferences.getToken()

    suspend fun deleteToken(){
        saveAuthPreferences.deleteAuthToken()
    }

    private fun generateToken(token: String): String {
        return "Bearer $token"
    }



    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: Api,
            saveAuthPreferences: SaveAuthPreferences,
            database: StoriesDatabase
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, saveAuthPreferences,database)
            }.also { instance = it }
    }


}