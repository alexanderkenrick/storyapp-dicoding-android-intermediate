package com.alexander.storyapp.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alexander.storyapp.R
import com.alexander.storyapp.ui.ViewModelFactory
import com.alexander.storyapp.ui.auth.LoginActivity
import com.alexander.storyapp.ui.home.HomeActivity

class WelcomeActivity : AppCompatActivity() {

    private val welcomeViewModel by viewModels<WelcomeViewModel> {
        ViewModelFactory(applicationContext)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_welcome)

        Thread.sleep(3000)

        welcomeViewModel.getSession().observe(this){
            if(it.token.isEmpty()){
                navigateTo(LoginActivity::class.java)
            }else{
                navigateTo(HomeActivity::class.java)
            }
        }
    }

    private fun navigateTo(activityClass : Class<out AppCompatActivity>){
        val intent = Intent(this@WelcomeActivity, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this.finish()
    }
}