package com.alexander.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alexander.storyapp.data.api.ApiService
import com.alexander.storyapp.data.response.auth.LoginResponse
import com.alexander.storyapp.data.response.auth.RegisterResponse
import com.alexander.storyapp.data.response.story.Story
import com.alexander.storyapp.data.response.story.StoryResponse
import com.alexander.storyapp.utils.AuthPreferences
import com.alexander.storyapp.utils.UserEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private var _loginResult = MutableLiveData<LoginResponse?>()
    var loginResult: MutableLiveData<LoginResponse?> = _loginResult

    private var _listStory = MutableLiveData<List<Story>>()
    var listStory: MutableLiveData<List<Story>> = _listStory

    suspend fun register(
        email: String,
        password: String,
        name: String
    ) : RegisterResponse {
        return apiService.register(name, email, password)
    }

    fun login(
        email: String,
        password: String,
    ){
        _isLoading.value = true
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody != null){
                        _loginResult.value = responseBody
                        _isLoading.value = false
                    }
                }else{
                    val responseBody  = response.errorBody()
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("AuthRepository Login", t.message.toString())
            }
        })
    }

    suspend fun saveSession(user : UserEntity){
        authPreferences.saveAuthSession(user)
        _isLoading.value = false
    }

    fun getSession() : Flow<UserEntity>{
        return authPreferences.getAuthSession()
    }

    suspend fun removeSession(){
        authPreferences.removeSession()
    }

    fun getStories(){
        _isLoading.value = true
        val client = apiService.getStories()
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if (response.isSuccessful){
                    _isLoading.value = false
                    _listStory.value = response.body()?.listStory
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("Repository", t.message.toString() )
            }

        })
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun clearInstance() {
            instance = null
        }

        fun getInstance(apiService: ApiService, authPreferences: AuthPreferences): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, authPreferences)
            }.also { instance = it }
    }
}