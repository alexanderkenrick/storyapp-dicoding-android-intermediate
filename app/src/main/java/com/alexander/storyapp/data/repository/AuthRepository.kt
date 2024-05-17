package com.alexander.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alexander.storyapp.data.api.ApiService
import com.alexander.storyapp.data.response.auth.RegisterResponse
import com.alexander.storyapp.utils.AuthPreferences

class AuthRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    suspend fun register(
        email: String,
        password: String,
        name: String
    ) : RegisterResponse {
        return apiService.register(name, email, password)
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