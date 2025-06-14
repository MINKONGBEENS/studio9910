package com.soze.studio9910

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.text.SimpleDateFormat
import java.util.*

class TodaysFeelingCompleteActivity : AppCompatActivity() {

    private lateinit var diaryName: TextView
    private lateinit var diaryContent: TextView
    private lateinit var diaryDate: TextView
    private lateinit var webtoonImage: ImageView
    private lateinit var loadingModal: View
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var loadingPercentage: TextView
    private lateinit var loadingProgressText: TextView

    private val loadingMessages = listOf(
        "그림체를 선정하는 중...",
        "캐릭터를 생성하는 중...",
        "배경을 그리는 중...",
        "스토리를 구성하는 중...",
        "마무리 작업 중..."
    )

    private val WEBTOON_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todays_feeling_complete)

        diaryName = findViewById(R.id.diary_name)
        diaryContent = findViewById(R.id.diary_content)
        diaryDate = findViewById(R.id.diary_date)
        webtoonImage = findViewById(R.id.webtoon_image)
        loadingModal = findViewById(R.id.webtoon_loading_modal)
        progressIndicator = findViewById(R.id.loading_progress)
        loadingPercentage = findViewById(R.id.loading_percentage)
        loadingProgressText = findViewById(R.id.loading_progress_text)

        loadTodayDiary()
        loadSelectedWebtoon()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // 화면이 다시 보일 때마다 선택된 웹툰 확인
        loadSelectedWebtoon()
    }

    private fun loadTodayDiary() {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)

        val title = prefs.getString("${today}_title", "오늘의 이야기")
        val content = prefs.getString("${today}_content", "")
        val timestamp = prefs.getLong("${today}_timestamp", System.currentTimeMillis())

        diaryName.text = title
        diaryContent.text = content

        // 날짜 포맷팅
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val dayCount = calculateDayCount() // 임시로 계산
        diaryDate.text = "${dayCount}번째 하루, ${dateFormat.format(Date(timestamp))}"
    }

    private fun loadSelectedWebtoon() {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)
        
        val isWebtoonSelected = prefs.getBoolean("${today}_webtoon_selected", false)
        
        if (isWebtoonSelected) {
            val selectedWebtoonResId = prefs.getInt("${today}_selected_webtoon", 0)
            if (selectedWebtoonResId != 0) {
                webtoonImage.setImageResource(selectedWebtoonResId)
                webtoonImage.visibility = View.VISIBLE
            }
        } else {
            webtoonImage.visibility = View.GONE
        }
    }

    private fun calculateDayCount(): Int {
        // 임시로 랜덤 숫자 반환 (실제로는 앱 시작일부터 계산)
        return (1..365).random()
    }

    private fun setupClickListeners() {
        // 뒤로가기 버튼
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // 북마크 버튼
        findViewById<ImageButton>(R.id.btn_bookmark).setOnClickListener {
            // TODO: 북마크 기능 구현
        }

        // 웹툰 생성 버튼
        findViewById<ImageButton>(R.id.btn_webtoon).setOnClickListener {
            showWebtoonLoadingModal()
        }
    }

    private fun showWebtoonLoadingModal() {
        // 모달 표시
        loadingModal.visibility = View.VISIBLE
        
        // 프로그레스 초기화
        progressIndicator.progress = 0
        loadingPercentage.text = loadingMessages[0]
        loadingProgressText.text = "0% 완료!"

        // 8초간 프로그레스 애니메이션
        val progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 8000 // 8초
            addUpdateListener { animator ->
                val progress = animator.animatedValue as Int
                progressIndicator.progress = progress
                loadingProgressText.text = "${progress}% 완료!"
                
                // 진행률에 따라 메시지 변경
                val messageIndex = when {
                    progress < 20 -> 0
                    progress < 40 -> 1
                    progress < 60 -> 2
                    progress < 80 -> 3
                    else -> 4
                }
                loadingPercentage.text = loadingMessages[messageIndex]
            }
        }
        progressAnimator.start()

        // 8초 후 웹툰 결과 화면으로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            loadingModal.visibility = View.GONE
            val intent = Intent(this, WebtoonResultActivity::class.java)
            startActivityForResult(intent, WEBTOON_REQUEST_CODE)
        }, 8000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WEBTOON_REQUEST_CODE && resultCode == RESULT_OK) {
            // 웹툰 선택 후 돌아왔을 때 선택된 웹툰 표시
            loadSelectedWebtoon()
        }
    }
} 