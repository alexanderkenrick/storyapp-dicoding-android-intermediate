package com.alexander.storyapp.ui.auth

import androidx.lifecycle.ViewModel
import com.alexander.storyapp.data.repository.AuthRepository
import com.alexander.storyapp.data.response.auth.RegisterResponse

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {


    suspend fun register(name : String, email : String, password : String): RegisterResponse {

        return authRepository.register(email, password, name)
    }

}