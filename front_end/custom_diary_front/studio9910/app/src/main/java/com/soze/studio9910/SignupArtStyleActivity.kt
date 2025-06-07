package com.soze.studio9910

import android.widget.ImageView
import com.soze.studio9910.utils.UIUtils

class SignupArtStyleActivity : BaseSignupActivity() {
    
    private var currentSelectedArtStyle: String? = null

    // 아트 스타일 이미지뷰들
    private lateinit var artStyle1: ImageView
    private lateinit var artStyle2: ImageView
    private lateinit var artStyle3: ImageView
    private lateinit var artStyle4: ImageView
    private lateinit var artStyle5: ImageView
    private lateinit var artStyle6: ImageView

    override fun getLayoutResId(): Int = R.layout.activity_signup_art_style

    override fun getNextActivityClass(): Class<*>? = null // 마지막 페이지이므로 특별 처리

    override fun isValidForNext(): Boolean = currentSelectedArtStyle != null

    override fun initSpecificViews() {
        artStyle1 = findViewById(R.id.artStyle1)
        artStyle2 = findViewById(R.id.artStyle2)
        artStyle3 = findViewById(R.id.artStyle3)
        artStyle4 = findViewById(R.id.artStyle4)
        artStyle5 = findViewById(R.id.artStyle5)
        artStyle6 = findViewById(R.id.artStyle6)
    }

    override fun setupSpecificListeners() {
        // 다음 버튼을 회원가입 완료로 변경하고 특별 처리
        nextButton.text = "회원가입 완료"
        nextButton.setOnClickListener {
            if (isValidForNext()) {
                goToMainPage()
            }
        }
        
        // 아트 스타일 이미지 클릭 리스너
        artStyle1.setOnClickListener { selectArtStyle("style1", artStyle1) }
        artStyle2.setOnClickListener { selectArtStyle("style2", artStyle2) }
        artStyle3.setOnClickListener { selectArtStyle("style3", artStyle3) }
        artStyle4.setOnClickListener { selectArtStyle("style4", artStyle4) }
        artStyle5.setOnClickListener { selectArtStyle("style5", artStyle5) }
        artStyle6.setOnClickListener { selectArtStyle("style6", artStyle6) }
    }

    override fun addSpecificExtras(intent: android.content.Intent) {
        intent.putExtra("selectedArtStyle", currentSelectedArtStyle)
    }
    
    private fun selectArtStyle(styleId: String, selectedImageView: ImageView) {
        // 이전 선택 해제
        resetAllArtStyles()
        
        // 새로운 선택 적용
        currentSelectedArtStyle = styleId
        UIUtils.updateSelectableItemBackground(this, selectedImageView, true,
            R.drawable.art_style_selected, R.drawable.art_style_unselected)
        
        updateNextButton(isValidForNext())
    }
    
    private fun resetAllArtStyles() {
        val artStyles = listOf(artStyle1, artStyle2, artStyle3, artStyle4, artStyle5, artStyle6)
        artStyles.forEach { artStyle ->
            UIUtils.updateSelectableItemBackground(this, artStyle, false,
                R.drawable.art_style_selected, R.drawable.art_style_unselected)
        }
    }
    
    private fun goToMainPage() {
        // 회원가입 완료 - 메인 페이지로 이동
        val intent = android.content.Intent(this, MainPageActivity::class.java).apply {
            putExtra("email", userEmail)
            putExtra("password", userPassword)
            putStringArrayListExtra("selectedGenres", selectedGenres)
            putExtra("selectedArtStyle", currentSelectedArtStyle)
        }
        startActivity(intent)
        
        // 회원가입 완료이므로 이전 액티비티들 모두 종료
        finishAffinity()
        
        // 화면 전환 애니메이션
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
} 