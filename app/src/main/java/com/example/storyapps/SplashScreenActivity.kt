package com.example.storyapps

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapps.MainActivity.Companion.EXTRA_TOKEN

import com.example.storyapps.databinding.ActivitySplashScreenBinding
import com.example.storyapps.viewModel.UserViewModel
import com.example.storyapps.viewModel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val viewModel: UserViewModel by viewModels { factory }
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        factory = ViewModelFactory.getInstance(this)
        lifecycleScope.launchWhenResumed {
            launch {
                delay(splashTime)
                viewModel.getToken().collect {
                    if (it.isNullOrEmpty()) {
                        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        intent.putExtra(EXTRA_TOKEN, it)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object{
        const val splashTime: Long = 2500
    }
}