package com.alexander.storyapp.data.api

import com.alexander.storyapp.data.response.auth.LoginResponse
import com.alexander.storyapp.data.response.auth.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
     suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

//    @GET("stories")
//    suspend fun getStories(
//        @Query("page") page: Int,
//        @Query("size") size: Int,
//    ) : StoriesResponse
}