package com.alexander.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexander.storyapp.R
import com.alexander.storyapp.databinding.ActivityHomeBinding
import com.alexander.storyapp.ui.StoryAdapter
import com.alexander.storyapp.ui.ViewModelFactory
import com.alexander.storyapp.ui.maps.MapsActivity
import com.alexander.storyapp.ui.upload.UploadActivity
import com.alexander.storyapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory(applicationContext)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel.isLoading.observe(this){
            showLoading(it)
        }

        setStoryList()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.btn_logout -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        showLoading(true)
                        homeViewModel.logOut()
                    }

                    val intent = Intent(this@HomeActivity, WelcomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this.finish()
                    true
                }
                R.id.btn_map ->{
                    val intent = Intent(this@HomeActivity, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        binding.fbCreate.setOnClickListener {
            val intent = Intent(this@HomeActivity, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(status: Boolean){
        binding.pgLoading.visibility = if(status) View.VISIBLE else View.GONE
    }

    private fun setStoryList() {
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter

        homeViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
            if (it == null) {
                with(binding.tvNotAvail) {
                    visibility = View.VISIBLE
                }
            } else {
                binding.tvNotAvail.visibility = View.GONE
            }
        }
    }
}