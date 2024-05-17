package com.alexander.storyapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexander.storyapp.R
import com.alexander.storyapp.ui.auth.RegisterActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        Thread.sleep(1000)
        setContentView(R.layout.activity_welcome)

        val intent = Intent(this@WelcomeActivity, RegisterActivity::class.java)
        startActivity(intent)
    }
}