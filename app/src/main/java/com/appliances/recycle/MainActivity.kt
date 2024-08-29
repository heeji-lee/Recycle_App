package com.appliances.recycle

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 로그인 버튼 클릭 이벤트 처리
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            // 로그인 처리 로직 구현
        }

        // 아이디 찾기, 비밀번호 찾기, 회원가입 이벤트 처리
        findViewById<TextView>(R.id.forgotId).setOnClickListener {
            // 아이디 찾기 로직 구현
        }

        findViewById<TextView>(R.id.forgotPassword).setOnClickListener {
            // 비밀번호 찾기 로직 구현
        }

        findViewById<TextView>(R.id.register).setOnClickListener {
            // 회원가입 로직 구현
        }
    }
}
