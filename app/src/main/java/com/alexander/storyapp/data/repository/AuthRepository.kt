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

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    var resultResponse = RegisterResponse(true, "Error while registering")
    //    suspend fun register(
//        email: String,
//        password: String,
//        name: String
//    ): LiveData<Result<RegisterResponse>> = liveData {}
//    fun register(
//        email: String,
//        password: String,
//        name: String
//    ): RegisterResponse {
//
//        _isLoading.value = true
//        Log.e("register repo", "Before Client")
//        val client = ApiConfig.getApiService("").register(name, email, password)
//        Log.e("register repo", "Before Queue")
//        client.enqueue(object : Callback<RegisterResponse> {
//            override fun onResponse(
//                call: Call<RegisterResponse>,
//                response: Response<RegisterResponse>
//            ) {
//                _isLoading.value = false
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null) {
//                        resultResponse = responseBody
//                    }
//                } else {
//                    val jsonInString = response.errorBody()?.string()
//                    val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
//                    val errorMessage = errorBody.message
//                    resultResponse = RegisterResponse(false, errorMessage)
//                    Log.e("register repo", "onFailureAtas: ${errorMessage}")
//                }
//            }
//
//            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
//                _isLoading.value = false
//                resultResponse = RegisterResponse(
//                    error = true,
//                    message = "Error"
//                )
//                Log.e("register repo", "onFailureBawah: ${t.message.toString()}")
//            }
//        })
//        return resultResponse
//    }

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