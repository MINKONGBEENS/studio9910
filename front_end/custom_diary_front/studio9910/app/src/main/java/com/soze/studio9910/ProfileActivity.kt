package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var settingsButton: ImageView
    private lateinit var profileImage: ShapeableImageView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var totalDiariesCount: TextView
    private lateinit var monthlyDiariesCount: TextView
    private lateinit var streakCount: TextView
    private lateinit var editProfileButton: LinearLayout
    private lateinit var notificationButton: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        setupClickListeners()
        setupBottomNavigation()
        loadUserProfile()
    }

    private fun initViews() {
        settingsButton = findViewById(R.id.settingsButton)
        profileImage = findViewById(R.id.profileImage)
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        totalDiariesCount = findViewById(R.id.totalDiariesCount)
        monthlyDiariesCount = findViewById(R.id.monthlyDiariesCount)
        streakCount = findViewById(R.id.streakCount)
        editProfileButton = findViewById(R.id.editProfileButton)
        notificationButton = findViewById(R.id.notificationButton)
    }

    private fun setupClickListeners() {
        // 설정 버튼 클릭 - SettingsActivity로 이동
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // 내 정보 수정 버튼
        editProfileButton.setOnClickListener {
            Toast.makeText(this, "내 정보 수정 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show()
        }

        // 알림 설정 버튼
        notificationButton.setOnClickListener {
            Toast.makeText(this, "알림 설정 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show()
        }

        // 프로필 이미지 클릭
        profileImage.setOnClickListener {
            Toast.makeText(this, "프로필 이미지 변경 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.navigation_profile // 현재 페이지 활성화
        
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
                    // 이미 프로필 화면이므로 아무 작업 안함
                    true
                }
                else -> false
            }
        }
    }

    private fun loadUserProfile() {
        // SharedPreferences에서 사용자 정보 로드
        val authPrefs = getSharedPreferences("auth", MODE_PRIVATE)
        val email = authPrefs.getString("email", "user@example.com")
        
        // 사용자 정보 설정
        userEmail.text = email
        
        // 일기 통계 로드
        loadDiaryStats()
    }

    private fun loadDiaryStats() {
        val diaryPrefs = getSharedPreferences("diary", MODE_PRIVATE)
        val allEntries = diaryPrefs.all
        
        // 총 일기 수 계산 (날짜 키로 저장된 것들만)
        val totalDiaries = allEntries.keys.count { key ->
            key.matches(Regex("\\d{8}")) && diaryPrefs.getBoolean(key, false)
        }
        
        // 이번 달 일기 수 계산
        val currentMonth = java.text.SimpleDateFormat("yyyyMM", java.util.Locale.getDefault())
            .format(java.util.Date())
        val monthlyDiaries = allEntries.keys.count { key ->
            key.startsWith(currentMonth) && key.matches(Regex("\\d{8}")) && 
            diaryPrefs.getBoolean(key, false)
        }
        
        // 연속 기록 계산 (간단한 버전)
        val streak = calculateStreak(diaryPrefs)
        
        // UI 업데이트
        totalDiariesCount.text = totalDiaries.toString()
        monthlyDiariesCount.text = monthlyDiaries.toString()
        streakCount.text = streak.toString()
    }

    private fun calculateStreak(prefs: android.content.SharedPreferences): Int {
        val dateFormat = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        val calendar = java.util.Calendar.getInstance()
        var streak = 0
        
        // 어제부터 거슬러 올라가면서 연속 기록 계산
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        
        for (i in 0 until 30) { // 최대 30일까지만 확인
            val dateString = dateFormat.format(calendar.time)
            if (prefs.getBoolean(dateString, false)) {
                streak++
                calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        
        return streak
    }

    // 오늘 다이어리 작성 여부 확인
    private fun hasTodayDiary(): Boolean {
        val today = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
            .format(java.util.Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)
        return prefs.getBoolean(today, false)
    }
} 