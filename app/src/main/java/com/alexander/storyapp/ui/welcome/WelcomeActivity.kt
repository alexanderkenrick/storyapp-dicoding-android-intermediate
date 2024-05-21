package com.alexander.storyapp.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.alexander.storyapp.ui.ViewModelFactory
import com.alexander.storyapp.ui.auth.LoginActivity
import com.alexander.storyapp.ui.home.HomeActivity

class WelcomeActivity : AppCompatActivity() {

    private val welcomeViewModel by viewModels<WelcomeViewModel> {
        ViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        welcomeViewModel.getSession().observe(this){
            if(it.token.isEmpty() || it.token == "null"){
                navigateTo(LoginActivity::class.java)
            }else if(it.token != "null"){
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