package com.appliances.recycle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.appliances.recycle.network.MyApplication
import com.appliances.recycle.repository.LoginRepository
import com.appliances.recycle.viewModel.LoginViewModel
import com.appliances.recycle.viewModelFactory.LoginViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = LoginRepository(MyApplication.instance)
        val factory = LoginViewModelFactory(repository)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        val loginButton = findViewById<Button>(R.id.btnLogin)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)

        // 로그인 버튼 클릭 이벤트 처리
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            // 로그인 처리 로직 구현
            val username = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            loginViewModel.login(username, password)
        }
        loginViewModel.loginResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }


//        // 아이디 찾기, 비밀번호 찾기, 회원가입 이벤트 처리
//        findViewById<TextView>(R.id.forgotId).setOnClickListener {
//            // 아이디 찾기 로직 구현
//        }

//        findViewById<TextView>(R.id.forgotPassword).setOnClickListener {
//            // 비밀번호 찾기 로직 구현
//        }

        // 회원가입 버튼 클릭 리스너
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            // 회원가입 화면으로 이동하는 예시 Intent
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
