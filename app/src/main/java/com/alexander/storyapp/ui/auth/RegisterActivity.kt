package com.alexander.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alexander.storyapp.R
import com.alexander.storyapp.data.response.auth.RegisterResponse
import com.alexander.storyapp.databinding.ActivityRegisterBinding
import com.alexander.storyapp.ui.ViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playAnimation()

        binding.tvToLogin.setOnClickListener {
            moveToLogin()
        }

        binding.btnRegister.setOnClickListener {
            showLoading(true)
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()


            if((!binding.edRegisterName.text.isEmpty()) && binding.edRegisterEmail.error != null && binding.edRegisterPassword.error != null){
                showToast(getString(R.string.authInputError))
            }else{
                lifecycleScope.launch {
                    showLoading(true)
                    try {
                        val response = registerViewModel.register(name, email, password)
                        showToast(response.message)

                        moveToLogin()
                    }catch (e : HttpException){
                        val errorBody = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                        showToast(errorResponse.message)
                    }
                }
            }
        }
    }

    private fun showLoading(status: Boolean){
        binding.pgLoading.visibility = if(status)View.VISIBLE else View.GONE
    }

    private fun showToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        showLoading(false)
    }

    private fun moveToLogin(){
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this.finish()
    }

    private fun playAnimation(){
        val logo =
            ObjectAnimator.ofFloat(binding.ivLogo, View.ALPHA, 1f).setDuration(200)
        val edName =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(200)
        val edEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(200)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(200)
        val btnRegister =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(200)
        val tvNavigate =
            ObjectAnimator.ofFloat(binding.tvToLogin, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                logo,
                edName,
                edEmail,
                edPassword,
                btnRegister,
                tvNavigate
            )
            startDelay = 300
        }.start()
    }
}