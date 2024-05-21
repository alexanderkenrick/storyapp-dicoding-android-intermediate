package com.alexander.storyapp.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.alexander.storyapp.data.repository.AuthRepository
import com.alexander.storyapp.data.response.story.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var isLoading : LiveData<Boolean> = authRepository.isLoading
    val uploadStatus: LiveData<Result<UploadResponse>> = authRepository.uploadStatus

    fun uploadStory(image: MultipartBody.Part, description: RequestBody){
        return authRepository.uploadStory(image, description)
    }
}