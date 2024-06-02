package com.alexander.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.alexander.storyapp.data.repository.AuthRepository
import com.alexander.storyapp.data.response.story.Story

class HomeViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var isLoading: LiveData<Boolean> = authRepository.isLoading
    val story: LiveData<PagingData<Story>> = authRepository.getStories().cachedIn(viewModelScope)

    suspend fun logOut() {
        authRepository.removeSession()
    }
}