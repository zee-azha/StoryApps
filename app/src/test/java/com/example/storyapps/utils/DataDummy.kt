package com.example.storyapps.utils

import com.example.storyapps.data.database.StoriesResponseItem
import com.example.storyapps.data.datalocal.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


object DataDummy {

    fun generateDummyStoryResponse(): StoriesResponse{
        val items = mutableListOf<Stories>()
        val error = false
        val message = "Stories fetched successfully"
        for (i in 0..100){
            val stories = Stories(
                id ="story-FvU4u0Vp2S3PMsFg",
                name ="Dimas",
                description= "Lorem Ipsum",
                photoUrl= "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt= "2022-01-08T06:34:18.598Z",
                lat= -10.212,
                lon= -16.002
            )
            items.add(stories)
        }
        return StoriesResponse(error,message,items)
    }

    fun generateDummyStoryList(): List<StoriesResponseItem>{
        val items = arrayListOf<StoriesResponseItem>()
        for (i in 0..100){
            val story = StoriesResponseItem(
                id ="story-FvU4u0Vp2S3PMsFg",
                name ="Dimas",
                description= "Lorem Ipsum",
                photoUrl= "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt= "2022-01-08T06:34:18.598Z",
                lat= -10.212,
                lon= -16.002
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyLoginResponse(): LoginResponse{
        val loginResult = ResultUser(
            userId = "user-yj5pc_LARC_AgK61",
            name = "Arif Faizin",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"
        )
        return LoginResponse(
            error = false,
            message = "success",
            result = loginResult
        )
    }
    fun generateDummyRegisterResponse(): RegisterResponse{
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }
    fun generateDummyMultipartFile(): MultipartBody.Part{
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }
    fun generateDummyRequestBody(): RequestBody {
        val dummyText = "text"
        return dummyText.toRequestBody()
    }

    fun generateDummyUploadResponse(): UploadResponse{
        return UploadResponse(
            error = false,
            message = "success"
        )
    }
    fun generateDummyToken() : String{
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"
    }
}