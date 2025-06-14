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

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.navigation_card // 현재 페이지 활성화
        
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
                    // 이미 저장소 화면이므로 아무 작업 안함
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