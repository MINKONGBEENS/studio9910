package com.soze.studio9910

import android.content.Intent
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat

class SignupArtStyleActivity : BaseSignupActivity() {
    private var selectedStyle: String? = null

    override fun getLayoutResId(): Int = R.layout.activity_signup_art_style

    override fun getNextActivityClass(): Class<*> = LoginActivity::class.java

    override fun isValidForNext(): Boolean = selectedStyle != null

    override fun initSpecificViews() {
        // 모든 아트 스타일 ImageView 찾기 및 초기 상태 설정
        val artStyles = listOf(
            R.id.artStyle1, R.id.artStyle2, R.id.artStyle3,
            R.id.artStyle4, R.id.artStyle5, R.id.artStyle6
        )
        
        artStyles.forEach { styleId ->
            findViewById<ImageView>(styleId)?.apply {
                background = ContextCompat.getDrawable(this@SignupArtStyleActivity, R.drawable.art_style_unselected)
            }
        }
    }

    override fun setupSpecificListeners() {
        // 모든 아트 스타일 카드에 클릭 리스너 설정
        val artStyleIds = listOf(
            R.id.artStyle1 to "style1",
            R.id.artStyle2 to "style2", 
            R.id.artStyle3 to "style3",
            R.id.artStyle4 to "style4",
            R.id.artStyle5 to "style5",
            R.id.artStyle6 to "style6"
        )
        
        artStyleIds.forEach { (styleId, styleName) ->
            findViewById<ImageView>(styleId)?.setOnClickListener { view ->
                Log.d("SignupArtStyleActivity", "Style clicked: $styleName")
                selectedStyle = styleName
                updateSelection(view as ImageView)
            }
        }
    }

    override fun addSpecificExtras(intent: Intent) {
        intent.putExtra("selectedArtStyle", selectedStyle)
    }

    private fun updateSelection(selectedCard: ImageView) {
        Log.d("SignupArtStyleActivity", "updateSelection called")
        
        // 모든 카드 선택 해제 (drawable 직접 변경)
        val allStyleIds = listOf(
            R.id.artStyle1, R.id.artStyle2, R.id.artStyle3,
            R.id.artStyle4, R.id.artStyle5, R.id.artStyle6
        )
        
        allStyleIds.forEach { styleId ->
            findViewById<ImageView>(styleId)?.apply {
                background = ContextCompat.getDrawable(this@SignupArtStyleActivity, R.drawable.art_style_unselected)
            }
        }
        
        // 선택된 카드만 선택 상태로 변경
        selectedCard.background = ContextCompat.getDrawable(this, R.drawable.art_style_selected)
        Log.d("SignupArtStyleActivity", "Selection background updated")
        
        // 다음 버튼 활성화
        updateNextButton(isValidForNext())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
} 