package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class TodaysFeelingWriteActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var btnCheck: ImageView
    private lateinit var diaryName: EditText
    private lateinit var diaryDate: TextView
    private lateinit var diaryContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todays_feeling_write)
        
        initViews()
        
        setupUI()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        btnCheck = findViewById(R.id.btn_check)
        diaryName = findViewById(R.id.diary_name)
        diaryDate = findViewById(R.id.diary_date)
        diaryContent = findViewById(R.id.diary_content)
    }

    private fun setupUI() {
        // 날짜 설정
        val today = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date())
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.KOREA).format(Date())
        val dayCount = calculateDayCount() // 며칠째인지 계산
        
        diaryDate.text = "${dayCount}번째 하루, $today"
    }

    private fun setupClickListeners() {
        // 뒤로가기 버튼
        btnBack.setOnClickListener {
            finish()
        }

        // 완료 버튼
        btnCheck.setOnClickListener {
            saveDiary()
        }
    }

    private fun saveDiary() {
        val title = diaryName.text.toString().trim()
        val content = diaryContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // SharedPreferences에 일기 저장
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)
        
        prefs.edit().apply {
            putBoolean(today, true) // 오늘 일기 작성 완료 표시
            putString("${today}_title", title)
            putString("${today}_content", content)
            putLong("${today}_timestamp", System.currentTimeMillis())
            apply()
        }

        Toast.makeText(this, "일기가 저장되었습니다.", Toast.LENGTH_SHORT).show()

        // 완료 페이지로 이동
        val intent = Intent(this, TodaysFeelingCompleteActivity::class.java)
        intent.putExtra("title", title)
        intent.putExtra("content", content)
        startActivity(intent)
        finish()
    }

    private fun calculateDayCount(): Int {
        // 간단히 올해 1월 1일부터 오늘까지의 일수 계산
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_YEAR)
    }
} 