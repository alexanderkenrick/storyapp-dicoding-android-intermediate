package com.alexander.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexander.storyapp.data.repository.AuthRepository
import com.alexander.storyapp.data.response.story.Story
import com.alexander.storyapp.utils.Result
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: AuthRepository) : ViewModel()  {

    private val _listStories = MutableLiveData<List<Story>>()
    val listStories: LiveData<List<Story>> = _listStories

    init {
        getStoriesLocation()
    }
    private fun getStoriesLocation() {
        viewModelScope.launch {
            val response = repository.getStoriesLocation()

            when (response) {
                is Result.Success -> {
                    _listStories.value = response.data.listStory
                }

                is Result.Error -> {

                }

                is Result.Loading -> {

                }
            }
        }
    }
}