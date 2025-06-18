package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.soze.studio9910.databinding.ActivityMainBinding
import com.google.android.material.card.MaterialCardView
import com.soze.studio9910.utils.FloatingMenuHelper
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var floatingMenuHelper: FloatingMenuHelper

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
        setupFloatingMenu()
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

    private fun setupFloatingMenu() {
        floatingMenuHelper = FloatingMenuHelper(this, binding.root)
        floatingMenuHelper.setupFloatingMenu()
    }



    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_feed // 현재 페이지 활성화
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_feed -> {
                    // 이미 피드 화면이므로 아무 작업 안함
                    true
                }
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_add -> {
                    // + 버튼 클릭 시 플로팅 메뉴 표시/숨김
                    if (floatingMenuHelper.isMenuVisible()) {
                        floatingMenuHelper.hideMenu()
                    } else {
                        floatingMenuHelper.showMenu()
                    }
                    true
                }
                R.id.navigation_storage -> {
                    startActivity(Intent(this, StorageActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupCardClickListeners() {
        // 새 일기 작성 버튼 (헤더의 + 버튼) - 플로팅 메뉴 표시/숨김
        binding.btnAdd.setOnClickListener {
            if (floatingMenuHelper.isMenuVisible()) {
                floatingMenuHelper.hideMenu()
            } else {
                floatingMenuHelper.showMenu()
            }
        }

        // 포스트 좋아요 버튼들 (예시)
        binding.btnLike1?.setOnClickListener {
            Toast.makeText(this, "좋아요!", Toast.LENGTH_SHORT).show()
        }

        binding.btnLike2?.setOnClickListener {
            Toast.makeText(this, "좋아요!", Toast.LENGTH_SHORT).show()
        }

        binding.btnLike3?.setOnClickListener {
            Toast.makeText(this, "좋아요!", Toast.LENGTH_SHORT).show()
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