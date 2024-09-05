package com.appliances.recycle

import android.content.Intent
import android.os.Bundle
import com.appliances.recycle.notice.NoticeListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button

class MainPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        // 수거신청 버튼 클릭 시 ProductActivity로 이동
        val requestPickupButton: Button = findViewById(R.id.btnRequestPickup)
        requestPickupButton.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
        }

        // 공지사항 버튼 클릭 시 NoticeListActivity로 이동
        val noticeButton: Button = findViewById(R.id.btnNotice)
        noticeButton.setOnClickListener {
            val intent = Intent(this, NoticeListActivity::class.java)
            startActivity(intent)
        }

        // BottomNavigationView 설정
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // 홈 버튼 클릭 시 MainPageActivity로 이동
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_alert -> {
                    // 공지사항 클릭 시 NoticeListActivity로 이동
                    val intent = Intent(this, NoticeListActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
