package com.example.storyapps.data.remote

import com.example.storyapps.data.datalocal.LoginResponse
import com.example.storyapps.data.datalocal.RegisterResponse
import com.example.storyapps.data.datalocal.StoriesResponse
import com.example.storyapps.data.datalocal.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface Api {

    @FormUrlEncoded
    @POST("register")
     suspend fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ):RegisterResponse

    @FormUrlEncoded
    @POST("login")
     suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat : RequestBody?,
        @Part("lon") lon : RequestBody?
    ): UploadResponse
}