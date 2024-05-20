package com.alexander.storyapp.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alexander.storyapp.data.repository.AuthRepository
import com.alexander.storyapp.utils.UserEntity

class WelcomeViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun getSession() : LiveData<UserEntity>{
        return authRepository.getSession().asLiveData()
    }

}