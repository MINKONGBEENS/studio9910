package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.soze.studio9910.databinding.LoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    private var isAutoLoginChecked = false

    // 테스트용 계정 정보
    companion object {
        private val TEST_ACCOUNTS = mapOf(
            "test@example.com" to "password123",
            "user@test.com" to "test1234",
            "admin@studio9910.com" to "admin9910"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // 이메일과 비밀번호 유효성 검사
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 로그인 검증
            if (validateLogin(email, password)) {
                // 로그인 성공 시 토큰 저장
                saveAuthToken(email)
                handleLoginSuccess()
                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "이메일 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupTermsActivity::class.java))
        }

        // 자동로그인 체크박스 설정
        setupAutoLoginCheckbox()

        // 테스트 계정 정보 로그 출력
        printTestAccounts()
    }

    private fun setupAutoLoginCheckbox() {
        val autoLoginCheckIcon = findViewById<ImageView>(R.id.autoLoginCheckIcon)
        val autoLoginLayout = findViewById<LinearLayout>(R.id.optionLayout)
        
        // SharedPreferences에서 자동로그인 설정 불러오기
        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        isAutoLoginChecked = sharedPreferences.getBoolean("auto_login", false)
        
        // 초기 상태 설정
        updateAutoLoginIcon(autoLoginCheckIcon)
        
        // 자동로그인 영역 클릭 리스너 (아이콘과 텍스트 모두 클릭 가능)
        val autoLoginClickListener = {
            isAutoLoginChecked = !isAutoLoginChecked
            updateAutoLoginIcon(autoLoginCheckIcon)
            
            // SharedPreferences에 저장
            sharedPreferences.edit().putBoolean("auto_login", isAutoLoginChecked).apply()
        }
        
        // 아이콘 클릭
        autoLoginCheckIcon.setOnClickListener { autoLoginClickListener() }
        
        // 텍스트 클릭 (전체 영역에서 자동로그인 부분만)
        findViewById<LinearLayout>(R.id.optionLayout).setOnClickListener { view ->
            // 클릭 위치가 자동로그인 영역인지 확인 (대략적으로 왼쪽 절반)
            val clickX = view.width / 3
            autoLoginClickListener()
        }
    }
    
    private fun updateAutoLoginIcon(iconView: ImageView) {
        if (isAutoLoginChecked) {
            iconView.setImageResource(R.drawable.ic_check_circle)
            iconView.setColorFilter(resources.getColor(android.R.color.holo_orange_light, null))
        } else {
            iconView.setImageResource(R.drawable.ic_check_circle_outline)
            iconView.setColorFilter(resources.getColor(android.R.color.darker_gray, null))
        }
    }

    private fun validateLogin(email: String, password: String): Boolean {
        return TEST_ACCOUNTS[email] == password
    }

    private fun saveAuthToken(email: String) {
        // 실제로는 서버에서 받은 토큰을 저장해야 하지만, 테스트를 위해 이메일을 토큰으로 사용
        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("token", email)
            putString("email", email)
            // 자동로그인이 체크되지 않은 경우 토큰을 임시로만 저장
            if (!isAutoLoginChecked) {
                putBoolean("temp_login", true)
            }
            apply()
        }
    }

    private fun handleLoginSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun printTestAccounts() {
        println("=== 테스트 계정 정보 ===")
        TEST_ACCOUNTS.forEach { (email, password) ->
            println("이메일: $email")
            println("비밀번호: $password")
            println("-------------------")
        }
    }
} 