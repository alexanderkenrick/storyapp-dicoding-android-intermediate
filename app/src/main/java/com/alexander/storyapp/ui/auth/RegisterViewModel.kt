package com.alexander.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.alexander.storyapp.data.repository.AuthRepository
import com.alexander.storyapp.data.response.auth.RegisterResponse

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

//    private var _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> = authRepository.isLoading

    suspend fun register(name : String, email : String, password : String): RegisterResponse {

        return authRepository.register(email, password, name)
//        var response: RegisterResponse
//        try {
//            val message = authRepository.register(name, email, password).message
//            response = RegisterResponse(false, "Success")
//        }catch (e:HttpException){
//            val jsonInString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
//            val errorMessage = errorBody.message
//            response = RegisterResponse(false, errorMessage)
//        }
//        return response
    }

}