package com.alexander.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.alexander.storyapp.data.repository.AuthRepository
import com.alexander.storyapp.data.response.story.Story

class HomeViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var isLoading: LiveData<Boolean> = authRepository.isLoading

    fun getStories(): LiveData<List<Story>?> {
        authRepository.getStories()
        return authRepository.listStory
    }

    suspend fun logOut(){
        authRepository.removeSession()
    }
}