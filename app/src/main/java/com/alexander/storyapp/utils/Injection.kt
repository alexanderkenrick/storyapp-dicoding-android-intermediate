package com.alexander.storyapp.utils

import android.content.Context
import android.util.Log
import com.alexander.storyapp.data.api.ApiConfig
import com.alexander.storyapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): AuthRepository {
        val preferences = AuthPreferences.getInstance(context.dataStore)
        val user = runBlocking { preferences.getAuthSession().first() }
        Log.d("Token Injection Check", user.token)
        val apiService = ApiConfig.getApiService(user.token)
        return AuthRepository(apiService, preferences)
    }
}