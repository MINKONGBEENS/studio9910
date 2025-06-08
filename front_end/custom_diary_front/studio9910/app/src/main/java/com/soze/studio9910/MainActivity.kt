package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.soze.studio9910.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그인 상태 확인
        if (!isLoggedIn()) {
            // 로그인되지 않은 경우 로그인 페이지로 이동
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 로그인된 경우 메인 화면 표시
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        return sharedPreferences.getString("token", null) != null
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // 이미 홈 화면이므로 아무 작업 안함
                    true
                }
                R.id.navigation_calendar -> {
                    // TODO: 캘린더 화면으로 전환
                    true
                }
                R.id.navigation_card -> {
                    // TODO: 카드 화면으로 전환
                    true
                }
                R.id.navigation_more -> {
                    // TODO: 더보기 화면으로 전환
                    true
                }
                else -> false
            }
        }
    }
}