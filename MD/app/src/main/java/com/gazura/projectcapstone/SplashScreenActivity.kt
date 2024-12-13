package com.gazura.projectcapstone

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.gazura.projectcapstone.token.SessionManager
import com.gazura.projectcapstone.welcome.WelcomeActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        sessionManager = SessionManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (sessionManager.isLoggedIn() && sessionManager.getToken() != null) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, WelcomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}