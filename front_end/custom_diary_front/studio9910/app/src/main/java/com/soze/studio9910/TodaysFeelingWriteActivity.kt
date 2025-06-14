package com.soze.studio9910

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class TodaysFeelingWriteActivity : AppCompatActivity() {

    private lateinit var diaryContent: EditText
    private lateinit var diaryName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todays_feeling_write)

        diaryContent = findViewById(R.id.diary_content)
        diaryName = findViewById(R.id.diary_name)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // 뒤로가기 버튼
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // 확인 버튼 (작성 완료)
        findViewById<ImageView>(R.id.btn_check).setOnClickListener {
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

        // SharedPreferences에 다이어리 저장
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)
        
        prefs.edit().apply {
            putBoolean(today, true) // 오늘 다이어리 작성 완료 표시
            putString("${today}_title", title)
            putString("${today}_content", content)
            putLong("${today}_timestamp", System.currentTimeMillis())
            apply()
        }

        Toast.makeText(this, "다이어리가 저장되었습니다!", Toast.LENGTH_SHORT).show()
        finish()
    }
} 