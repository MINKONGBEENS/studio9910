package com.soze.studio9910

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.soze.studio9910.databinding.ActivitySignupArtStyleBinding

class SignupArtStyleActivity : BaseSignupActivity() {
    private lateinit var binding: ActivitySignupArtStyleBinding
    private var selectedStyle: String? = null

    override fun getLayoutResId(): Int = R.layout.activity_signup_art_style

    override fun getNextActivityClass(): Class<*> = LoginActivity::class.java

    override fun isValidForNext(): Boolean = selectedStyle != null

    override fun initSpecificViews() {
        // View Binding 초기화
        binding = ActivitySignupArtStyleBinding.bind(findViewById(android.R.id.content))
        
        // 초기 상태 설정
        binding.artStyle1.isSelected = false
        binding.artStyle2.isSelected = false
        binding.artStyle3.isSelected = false
    }

    override fun setupSpecificListeners() {
        // 각 스타일 카드에 클릭 리스너 설정
        binding.artStyle1.setOnClickListener {
            selectedStyle = "style1"
            updateSelection(binding.artStyle1)
        }
        binding.artStyle2.setOnClickListener {
            selectedStyle = "style2"
            updateSelection(binding.artStyle2)
        }
        binding.artStyle3.setOnClickListener {
            selectedStyle = "style3"
            updateSelection(binding.artStyle3)
        }
    }

    override fun addSpecificExtras(intent: Intent) {
        intent.putExtra("selectedArtStyle", selectedStyle)
    }

    private fun updateSelection(selectedCard: ImageView) {
        // 모든 카드 선택 해제
        binding.artStyle1.isSelected = false
        binding.artStyle2.isSelected = false
        binding.artStyle3.isSelected = false
        
        // 선택된 카드만 선택 상태로 변경
        selectedCard.isSelected = true
        
        // 다음 버튼 활성화
        updateNextButton(isValidForNext())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
} 