package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.soze.studio9910.databinding.ActivityMainBinding
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

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

        // 임시 로그인 플래그 제거 (앱이 정상적으로 시작되었으므로)
        clearTempLoginFlag()

        // 로그인된 경우 메인 화면 표시
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()
        setupCardClickListeners()
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val autoLogin = sharedPreferences.getBoolean("auto_login", false)
        val tempLogin = sharedPreferences.getBoolean("temp_login", false)
        
        // 토큰이 있고 자동로그인이 설정되어 있거나, 임시 로그인 상태인 경우
        return !token.isNullOrEmpty() && (autoLogin || tempLogin)
    }

    private fun clearTempLoginFlag() {
        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        sharedPreferences.edit().remove("temp_login").apply()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_home // 현재 페이지 활성화
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // 이미 홈 화면이므로 아무 작업 안함
                    true
                }
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_card -> {
                    startActivity(Intent(this, StorageActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_more -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupCardClickListeners() {
        // Today's Feeling 카드 클릭 리스너
        val cardTodayFeeling = findViewById<MaterialCardView>(R.id.cardTodayFeeling)
        cardTodayFeeling.setOnClickListener {
            if (hasTodayDiary()) {
                // 이미 작성한 경우 - 완료 페이지로
                val intent = Intent(this, TodaysFeelingCompleteActivity::class.java)
                startActivity(intent)
            } else {
                // 작성하지 않은 경우 - 작성 페이지로
                val intent = Intent(this, TodaysFeelingWriteActivity::class.java)
                startActivity(intent)
            }
        }

        // Daily Mytoon 카드 클릭 리스너 (추후 구현)
        val cardDailyMytoon = findViewById<MaterialCardView>(R.id.cardDailyMytoon)
        cardDailyMytoon.setOnClickListener {
            Toast.makeText(this, "구현중입니다", Toast.LENGTH_SHORT).show()
        }

        // Weekly Mytoon 카드 클릭 리스너 (추후 구현)
        val cardWeeklyMytoon = findViewById<MaterialCardView>(R.id.cardWeeklyMytoon)
        cardWeeklyMytoon.setOnClickListener {
            Toast.makeText(this, "구현중입니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 오늘 다이어리 작성 여부 확인 (SharedPreferences 사용)
    private fun hasTodayDiary(): Boolean {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)
        return prefs.getBoolean(today, false)
    }

    // 다이어리 작성 완료 시 호출할 함수 (다른 Activity에서 사용)
    fun markTodayDiaryAsCompleted() {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)
        prefs.edit().putBoolean(today, true).apply()
    }
}