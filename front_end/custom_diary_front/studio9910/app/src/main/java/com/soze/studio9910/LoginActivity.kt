package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.soze.studio9910.databinding.LoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding

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

        // 테스트 계정 정보 로그 출력
        printTestAccounts()
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