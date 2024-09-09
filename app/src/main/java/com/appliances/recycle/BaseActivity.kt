package com.appliances.recycle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appliances.recycle.notice.NoticeListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // 공통 툴바 설정 메서드
    protected fun setupToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 뒤로가기 버튼 클릭 이벤트
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // 공통 BottomNavigationView 설정 메서드
    protected fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // 홈으로 이동
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_alert -> {
                    // 공지사항으로 이동
                    val intent = Intent(this, NoticeListActivity::class.java)
                    startActivity(intent)
                    true
                }
                // 나머지 BottomNavigation 항목들도 여기에 추가 가능
                else -> false
            }
        }
    }
}
