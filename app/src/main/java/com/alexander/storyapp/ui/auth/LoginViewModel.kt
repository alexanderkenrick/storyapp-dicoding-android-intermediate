package com.alexander.storyapp.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexander.storyapp.data.repository.AuthRepository
import com.alexander.storyapp.data.response.auth.LoginResponse
import com.alexander.storyapp.utils.UserEntity

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    val loginObject: MutableLiveData<LoginResponse?> = authRepository.loginResult

    val isLoading: MutableLiveData<Boolean> = authRepository.isLoading
    fun login(email : String, password : String){
        return authRepository.login(email, password)
    }

    suspend fun saveSession(user : UserEntity){
        return authRepository.saveSession(user)
    }
}