package com.soze.studio9910

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class WebtoonResultActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var adapter: WebtoonCardAdapter

    // 웹툰 이미지 리소스 리스트
    private val webtoonImages = listOf(
        R.drawable.sample_webtoon,
        R.drawable.sample_webtoon2
    )

    private val STORAGE_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_webtoon_result)

        viewPager = findViewById(R.id.webtoonViewPager)
        indicatorContainer = findViewById(R.id.indicatorContainer)

        // 어댑터 설정
        adapter = WebtoonCardAdapter(webtoonImages)
        viewPager.adapter = adapter

        // 인디케이터 설정
        setupIndicators(webtoonImages.size)
        setCurrentIndicator(0)

        // 페이지 변경 리스너
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentIndicator(position)
            }
        })

        // 뒤로가기 버튼
        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
        }

        // 다운로드 버튼
        findViewById<View>(R.id.downloadButton).setOnClickListener {
            if (checkStoragePermission()) {
                downloadCurrentImage()
            } else {
                requestStoragePermission()
            }
        }

        // 선택 버튼
        findViewById<View>(R.id.selectButton).setOnClickListener {
            val currentPosition = viewPager.currentItem
            selectWebtoon(currentPosition)
        }
    }

    private fun setupIndicators(count: Int) {
        indicatorContainer.removeAllViews()
        for (i in 0 until count) {
            val indicator = View(this).apply {
                val size = 8.dpToPx()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    if (i != 0) leftMargin = 8.dpToPx()
                }
                setBackgroundResource(R.drawable.indicator_dot_unselected)
            }
            indicatorContainer.addView(indicator)
        }
    }

    private fun setCurrentIndicator(position: Int) {
        for (i in 0 until indicatorContainer.childCount) {
            val indicator = indicatorContainer.getChildAt(i)
            indicator.setBackgroundResource(
                if (i == position) R.drawable.indicator_dot_selected
                else R.drawable.indicator_dot_unselected
            )
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // Android 10 이상에서는 Scoped Storage 사용
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadCurrentImage()
            } else {
                Toast.makeText(this, "저장 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadCurrentImage() {
        val currentPosition = viewPager.currentItem
        val imageResId = webtoonImages[currentPosition]
        
        try {
            // 리소스에서 Bitmap 가져오기
            val drawable = ContextCompat.getDrawable(this, imageResId)
            val bitmap = (drawable as BitmapDrawable).bitmap

            // 파일명 생성
            val fileName = "webtoon_${System.currentTimeMillis()}.jpg"

            val saved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveImageToGalleryQ(bitmap, fileName)
            } else {
                saveImageToGalleryLegacy(bitmap, fileName)
            }

            if (saved) {
                Toast.makeText(this, "이미지가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "이미지 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToGalleryQ(bitmap: Bitmap, fileName: String): Boolean {
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                val outputStream = contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun saveImageToGalleryLegacy(bitmap: Bitmap, fileName: String): Boolean {
        return try {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(picturesDir, fileName)
            
            val outputStream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()

            // 갤러리에 스캔 요청
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DATA, file.absolutePath)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun selectWebtoon(position: Int) {
        // 선택된 웹툰에 스탬프 표시
        adapter.setSelectedPosition(position)
        
        Toast.makeText(this, "웹툰이 선택되었습니다!", Toast.LENGTH_SHORT).show()
        
        // 선택된 웹툰 정보를 SharedPreferences에 저장
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val prefs = getSharedPreferences("diary", MODE_PRIVATE)
        val selectedImageResId = webtoonImages[position]
        
        prefs.edit().apply {
            putInt("${today}_selected_webtoon", selectedImageResId)
            putBoolean("${today}_webtoon_selected", true)
            apply()
        }
    }

    // 뒤로가기 버튼 오버라이드
    override fun onBackPressed() {
        // 웹툰이 선택된 상태라면 결과와 함께 돌아가기
        if (adapter.getSelectedPosition() != -1) {
            setResult(RESULT_OK)
        }
        super.onBackPressed()
    }

    // dp를 px로 변환하는 확장 함수
    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()
} 