package com.alexander.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.alexander.storyapp.data.api.ApiService
import com.alexander.storyapp.data.database.StoryDatabase
import com.alexander.storyapp.data.paging.StoryRemoteMediator
import com.alexander.storyapp.data.response.auth.LoginResponse
import com.alexander.storyapp.data.response.auth.RegisterResponse
import com.alexander.storyapp.data.response.story.Story
import com.alexander.storyapp.data.response.story.StoryResponse
import com.alexander.storyapp.data.response.story.UploadResponse
import com.alexander.storyapp.utils.AuthPreferences
import com.alexander.storyapp.utils.UserEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences,
    private val storyDatabase: StoryDatabase
) {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private var _loginResult = MutableLiveData<LoginResponse?>()
    var loginResult: MutableLiveData<LoginResponse?> = _loginResult

    private val _uploadStatus = MutableLiveData<Result<UploadResponse>>()
    val uploadStatus: LiveData<Result<UploadResponse>> = _uploadStatus

    suspend fun register(
        email: String,
        password: String,
        name: String
    ): RegisterResponse {
        return apiService.register(name, email, password)
    }

    fun login(
        email: String,
        password: String,
    ) {
        _isLoading.value = true
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _loginResult.value = responseBody
                        _isLoading.value = false
                    }
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

            }
        })
    }

    suspend fun saveSession(user: UserEntity) {
        authPreferences.saveAuthSession(user)
        _isLoading.value = false
    }

    fun getSession(): Flow<UserEntity> {
        return authPreferences.getAuthSession()
    }

    suspend fun removeSession() {
        loginResult.value = null
        authPreferences.removeSession()
    }

    fun getStories(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }
        ).liveData
    }

    suspend fun getStoriesLocation(): com.alexander.storyapp.utils.Result<StoryResponse> {
        return try {
            val response = apiService.getStoriesWithLocation()
            com.alexander.storyapp.utils.Result.Success(response)
        } catch (e: Exception) {
            com.alexander.storyapp.utils.Result.Error(e.message ?: "Error while retrieving data")
        }
    }


    fun uploadStory(image: MultipartBody.Part, description: RequestBody, lat: RequestBody?= null, lon: RequestBody?=null) {
        _isLoading.value = true
        val client = apiService.uploadStory(image, description, lat,  lon)
        client.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _uploadStatus.value = Result.success(response.body()!!)
                } else {
                    val errorResponse =
                        Gson().fromJson(response.errorBody()?.string(), UploadResponse::class.java)
                    _uploadStatus.value = Result.failure(Exception(errorResponse.message))
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                _isLoading.value = false
                _uploadStatus.value = Result.failure(t)
            }
        })
    }
}