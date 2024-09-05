package com.appliances.recycle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 로그인 버튼 클릭 리스너
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            // 로그인 처리 로직 구현 (예: 로그인 API 호출 등)
            Toast.makeText(this, "로그인 버튼 클릭됨", Toast.LENGTH_SHORT).show()
        }

        // 회원가입 버튼 클릭 리스너
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            // 회원가입 화면으로 이동하는 예시 Intent
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
