package com.gazura.projectcapstone.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gazura.projectcapstone.MainActivity
import com.gazura.projectcapstone.databinding.ActivityLoginBinding
import com.gazura.projectcapstone.register.RegisterActivity
import com.gazura.projectcapstone.token.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private var isNavigating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                }
                password.length < 8 -> {
                    Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                }
                !isUserRegistered(email, password) -> {
                    Toast.makeText(this, "User not registered. Please sign up first.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    showLoading(true)
                    // Simulate login logic
                    Handler(Looper.getMainLooper()).postDelayed({
                        val isLoginSuccessful = true // Replace with actual login logic
                        val token = "user_token" // Replace with actual token

                        if (isLoginSuccessful) {
                            sessionManager.saveLoginSession(token)
                            sessionManager.saveEmail(email)
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            showLoading(false)
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }, 3000)
                }
            }
        }

        binding.tvDontHaveAccount.setOnClickListener {
            if (!isNavigating) {
                isNavigating = true
                binding.tvDontHaveAccount.isEnabled = false
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun isUserRegistered(email: String, password: String): Boolean {
        val registeredEmail = sessionManager.getEmail()
        val registeredPassword = sessionManager.getPassword()
        return email == registeredEmail && password == registeredPassword
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
    }
}