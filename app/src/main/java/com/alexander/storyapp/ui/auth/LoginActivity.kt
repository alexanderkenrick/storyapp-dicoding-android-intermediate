package com.alexander.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alexander.storyapp.R
import com.alexander.storyapp.data.response.auth.LoginResponse
import com.alexander.storyapp.databinding.ActivityLoginBinding
import com.alexander.storyapp.ui.ViewModelFactory
import com.alexander.storyapp.utils.UserEntity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.finish()
        }

        binding.btnLogin.setOnClickListener {

            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            if(binding.edLoginEmail.error != null || binding.edLoginPassword.error != null){
                showToast(getString(R.string.authInputError))
                return@setOnClickListener
            }

            if((email.isEmpty() || password.isEmpty())){
                showToast(getString(R.string.loginEmpty))
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.Main){
                loginProcess(email, password)
            }

            with(loginViewModel){
                isLoading.observe(this@LoginActivity){
                    showLoading(it)
                }

                loginObject.observe(this@LoginActivity){
                    if (it != null) {
                        if(it.error){
                            showToast(it.message)
                        }else{
                            val user = UserEntity(it.loginResult.userId.toString(), it.loginResult.name.toString(), it.loginResult.token.toString())
                            lifecycleScope.launch {
                                showLoading(true)
                                loginViewModel.saveSession(user)
                                showToast("Session Saved")
                                Log.v("Session save", "SAVED")
                            }
                        }
                    }
                }
            }
        }
    }

    suspend private fun loginProcess(email : String, password : String){
        try {
            loginViewModel.login(email, password)
            showToast(getString(R.string.loginSuccess))
        } catch (e : HttpException){
            showLoading(false)
            val body = e.response()?.errorBody().toString()
            val response = Gson().fromJson(body, LoginResponse::class.java)
            showToast(response.message)
        }
    }

    private fun showLoading(status: Boolean){
        binding.pgLoading.visibility = if(status) View.VISIBLE else View.GONE
    }

    private fun showToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        showLoading(false)
    }
}