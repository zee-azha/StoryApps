package com.example.storyapps

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapps.databinding.ActivityLoginBinding
import com.example.storyapps.utils.Resource
import com.example.storyapps.viewModel.UserViewModel
import com.example.storyapps.viewModel.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: UserViewModel by viewModels { factory }
    private var job: Job = Job()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        factory = ViewModelFactory.getInstance(this)
        setupView()
        playAnimation()

        binding.apply {
            passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
            emailEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            btnSignin.setOnClickListener {
                loginUser()
            }
            signup.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun loginUser() {
        binding.apply {
            val password = passwordEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            showLoading(true)
            lifecycleScope.launchWhenResumed {
                if (job.isActive) job.cancel()
                job = launch {
                    viewModel.login(email, password).collect { result ->
                        when (result){
                            is Resource.Success -> {
                                result.data?.result?.token.let {  token ->
                                    viewModel.saveToken(token!!)
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.putExtra(MainActivity.EXTRA_TOKEN, token)
                                    startActivity(intent)
                                    finish()
                                    showLoading(false)
                                }

                            }
                            is Resource.Failure ->{
                                Toast.makeText(
                                    this@LoginActivity,
                                    resources.getString(R.string.login_error_message),
                                    Toast.LENGTH_LONG
                                ).show()
                                showLoading(false)
                            }
                            is Resource.Loading ->{
                                showLoading(true)
                            }

                        }
                    }
                }
            }
        }
    }
    private fun setButtonEnable() {
        binding.apply {
            val password = passwordEditText.text
            val email = emailEditText.text
            btnSignin.isEnabled = password.toString().length >= 6 && Patterns.EMAIL_ADDRESS.matcher(
                email.toString()
            ).matches()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        when(isLoading){
            true ->{
                binding.progressBar.visibility = View.VISIBLE
            }
            false ->{
                binding.progressBar.visibility = View.GONE
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        binding.apply {
            val email =
                ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
            val password =
                ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
            val siginButton =
                ObjectAnimator.ofFloat(binding.btnSignin, View.ALPHA, 1f).setDuration(500)
            val tvSignup = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
            val signUp = ObjectAnimator.ofFloat(binding.signup, View.ALPHA, 1f).setDuration(500)
            AnimatorSet().apply {
                playSequentially(email, password, siginButton, tvSignup, signUp)
                startDelay = 500
            }.start()
        }
    }
}