package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class StorageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        setupBottomNavigation()
        setupClickListeners()
    }

    // 오늘 다이어리 작성 여부 확인
    private fun hasTodayDiary(): Boolean {
        val today = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
            .format(java.util.Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)
        return prefs.getBoolean(today, false)
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.navigation_storage // 현재 페이지 활성화
        
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
                    // 이미 스토리지 화면이므로 아무 작업 안함
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

    private fun setupClickListeners() {
        // 북마크 전체보기 버튼
        findViewById<ImageView>(R.id.btnBookmarkViewAll).setOnClickListener {
            Toast.makeText(this, "북마크 전체보기", Toast.LENGTH_SHORT).show()
            // TODO: 북마크 전체보기 페이지로 이동
        }

        // 최근 작성 전체보기 버튼
        findViewById<ImageView>(R.id.btnRecentViewAll).setOnClickListener {
            Toast.makeText(this, "최근 작성 전체보기", Toast.LENGTH_SHORT).show()
            // TODO: 최근 작성 전체보기 페이지로 이동
        }

        // 북마크 카드들 클릭 이벤트
        findViewById<MaterialCardView>(R.id.bookmarkCard1).setOnClickListener {
            openDiaryComplete("북마크된 다이어리 1")
        }
        findViewById<MaterialCardView>(R.id.bookmarkCard2).setOnClickListener {
            openDiaryComplete("북마크된 다이어리 2")
        }
        findViewById<MaterialCardView>(R.id.bookmarkCard3).setOnClickListener {
            openDiaryComplete("북마크된 다이어리 3")
        }

        // 최근 작성 카드들 클릭 이벤트
        findViewById<MaterialCardView>(R.id.recentCard1).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 1")
        }
        findViewById<MaterialCardView>(R.id.recentCard2).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 2")
        }
        findViewById<MaterialCardView>(R.id.recentCard3).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 3")
        }
        findViewById<MaterialCardView>(R.id.recentCard4).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 4")
        }
        findViewById<MaterialCardView>(R.id.recentCard5).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 5")
        }
        findViewById<MaterialCardView>(R.id.recentCard6).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 6")
        }
        findViewById<MaterialCardView>(R.id.recentCard7).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 7")
        }
        findViewById<MaterialCardView>(R.id.recentCard8).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 8")
        }
        findViewById<MaterialCardView>(R.id.recentCard9).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 9")
        }
        findViewById<MaterialCardView>(R.id.recentCard10).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 10")
        }
        findViewById<MaterialCardView>(R.id.recentCard11).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 11")
        }
        findViewById<MaterialCardView>(R.id.recentCard12).setOnClickListener {
            openDiaryComplete("최근 작성 다이어리 12")
        }
    }

    private fun openDiaryComplete(diaryTitle: String) {
        val intent = Intent(this, TodaysFeelingCompleteActivity::class.java)
        intent.putExtra("diary_title", diaryTitle)
        startActivity(intent)
    }
} 