package com.alexander.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
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
import com.alexander.storyapp.ui.home.HomeActivity
import com.alexander.storyapp.utils.UserEntity
import com.google.gson.Gson
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
        playAnimation()

        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.finish()
        }

        binding.btnLogin.setOnClickListener {
            binding.btnLogin.isEnabled = false

            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            if(binding.edLoginEmail.error != null || binding.edLoginPassword.error != null){
                showToast(getString(R.string.authInputError))
                binding.btnLogin.isEnabled = true
                return@setOnClickListener
            }

            if((email.isEmpty() || password.isEmpty())){
                binding.btnLogin.isEnabled = true
                showToast(getString(R.string.loginEmpty))
                return@setOnClickListener
            }

            loginProcess(email, password)

            with(loginViewModel){
                isLoading.observe(this@LoginActivity){
                    showLoading(it)
                }

                loginObject.observe(this@LoginActivity){
                    if (it != null) {
                        if (it.error) {
                            showToast(it.message)
                        }else{
                            binding.btnLogin.isEnabled = false
                            val user = UserEntity(it.loginResult?.userId.toString(), it.loginResult?.name.toString(), it.loginResult?.token.toString())
                            lifecycleScope.launch {
                                loginViewModel.saveSession(user)
                                showToast(getString(R.string.loginSuccess))
                                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else{
                        showToast("Login Failed")
                    }
                }
            }

            binding.btnLogin.isEnabled = true
        }
    }

    private fun loginProcess(email : String, password : String){
        try {
            showLoading(true)
            loginViewModel.login(email, password)
            showLoading(false)
        } catch (e : HttpException){
            binding.btnLogin.isEnabled = true
            showLoading(false)
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            showToast(errorResponse.message)
        }
    }

    private fun showLoading(status: Boolean){
        binding.pgLoading.visibility = if(status) View.VISIBLE else View.GONE
    }

    private fun showToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        showLoading(false)
    }

    private fun playAnimation(){
        val logo =
            ObjectAnimator.ofFloat(binding.ivLogo, View.ALPHA, 1f).setDuration(200)
        val edEmail =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(200)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(200)
        val btnLogin =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(200)
        val tvNavigate =
            ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                logo,
                edEmail,
                edPassword,
                btnLogin,
                tvNavigate
            )
            startDelay = 300
        }.start()
    }
}