package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing)

        // 3초 후에 로그인 상태 확인하여 적절한 화면으로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            if (isLoggedIn()) {
                // 로그인된 경우 메인 화면으로
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // 로그인되지 않은 경우 로그인 화면으로
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 3000)
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val autoLogin = sharedPreferences.getBoolean("auto_login", false)
        val tempLogin = sharedPreferences.getBoolean("temp_login", false)
        
        // 토큰이 있고 자동로그인이 설정되어 있거나, 임시 로그인 상태인 경우
        return !token.isNullOrEmpty() && (autoLogin || tempLogin)
    }
} 