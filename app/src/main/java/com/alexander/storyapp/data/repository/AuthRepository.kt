package com.alexander.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alexander.storyapp.data.api.ApiService
import com.alexander.storyapp.data.response.auth.LoginResponse
import com.alexander.storyapp.data.response.auth.RegisterResponse
import com.alexander.storyapp.utils.AuthPreferences
import com.alexander.storyapp.utils.UserEntity
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

    suspend fun register(
        email: String,
        password: String,
        name: String
    ) : RegisterResponse {
        return apiService.register(name, email, password)
    }

    internal fun login(
        email: String,
        password: String,
    ){
        _isLoading.value = true
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false

                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody != null){
                        _loginResult.value = responseBody
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("AuthRepository Login", t.message.toString())
            }
        })
    }

    suspend fun saveSession(user : UserEntity){
        authPreferences.saveAuthSession(user)
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