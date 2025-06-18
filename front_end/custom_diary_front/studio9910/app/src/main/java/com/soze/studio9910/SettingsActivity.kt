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

    // 오늘 다이어리 작성 여부 확인
    private fun hasTodayDiary(): Boolean {
        val today = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
            .format(java.util.Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)
        return prefs.getBoolean(today, false)
    }

    private fun setupClickListeners() {
        // 뒤로가기 버튼
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            // 프로필 페이지로 이동
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
        
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
        // 설정 페이지는 프로필에서 접근하므로 프로필을 활성화
        bottomNavigation.selectedItemId = R.id.navigation_profile
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_feed -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_add -> {
                    // + 버튼 클릭 시 일기 작성 페이지로
                    if (hasTodayDiary()) {
                        // 이미 작성한 경우 - 완료 페이지로
                        val intent = Intent(this, TodaysFeelingCompleteActivity::class.java)
                        startActivity(intent)
                    } else {
                        // 작성하지 않은 경우 - 작성 페이지로
                        val intent = Intent(this, TodaysFeelingWriteActivity::class.java)
                        startActivity(intent)
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
} 