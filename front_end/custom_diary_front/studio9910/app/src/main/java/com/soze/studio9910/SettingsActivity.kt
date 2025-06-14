package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupClickListeners() {
        // 로그아웃 버튼
        val logoutButton = findViewById<LinearLayout>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("로그아웃")
            .setMessage("정말 로그아웃하시겠습니까?")
            .setPositiveButton("로그아웃") { _, _ ->
                performLogout()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun performLogout() {
        // SharedPreferences에서 로그인 정보 삭제
        val authPrefs = getSharedPreferences("auth", MODE_PRIVATE)
        authPrefs.edit().clear().apply()

        // 다이어리 정보도 삭제 (선택사항)
        val diaryPrefs = getSharedPreferences("diary", MODE_PRIVATE)
        diaryPrefs.edit().clear().apply()

        // 로그인 화면으로 이동하고 현재 액티비티 스택 모두 제거
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.navigation_more // 현재 페이지 활성화
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
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
                    // 이미 설정 화면이므로 아무 작업 안함
                    true
                }
                else -> false
            }
        }
    }
} 