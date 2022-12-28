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
import com.example.storyapps.databinding.ActivityRegisterBinding
import com.example.storyapps.utils.Resource
import com.example.storyapps.viewModel.UserViewModel
import com.example.storyapps.viewModel.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: UserViewModel by viewModels { factory }
    private var job: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        playAnimation()
        factory = ViewModelFactory.getInstance(this)
        binding.apply {
            btnSignup.setOnClickListener {
                registerUser()
            }
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
            usernameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            login.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }

    private fun registerUser() {
        val name = binding.usernameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        showLoading(true)
        lifecycleScope.launchWhenResumed {
            if (job.isActive) job.cancel()
            job = launch {
                viewModel.register(name, email, password).collect { result ->
                    when(result){
                        is Resource.Success->{
                            intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                            showLoading(false)
                        }
                        is Resource.Failure->{
                            Toast.makeText(
                                this@RegisterActivity,
                                resources.getString(R.string.register_error_message),
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

    private fun setButtonEnable() {
        binding.apply {
            val password = passwordEditText.text
            val email = emailEditText.text
            val username = usernameEditText.text
            btnSignup.isEnabled =
                password.toString().length >= 6 && Patterns.EMAIL_ADDRESS.matcher(email.toString())
                    .matches() && username.toString().isNotEmpty()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
        binding.apply {
            val username =
                ObjectAnimator.ofFloat(binding.usernameEditText, View.ALPHA, 1f).setDuration(500)
            val email =
                ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
            val password =
                ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
            val sigunButton =
                ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(500)
            val tvSignup = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
            val signUp = ObjectAnimator.ofFloat(binding.login, View.ALPHA, 1f).setDuration(500)
            AnimatorSet().apply {
                playSequentially(username, email, password, sigunButton, tvSignup, signUp)
                startDelay = 500
            }.start()
        }

    }
}