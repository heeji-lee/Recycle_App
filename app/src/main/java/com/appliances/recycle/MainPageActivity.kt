package com.appliances.recycle

import android.content.Intent
import android.os.Bundle
import com.appliances.recycle.notice.NoticeListActivity
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

        // 신청현황 버튼 클릭 시 ProductListActivity로 이동
        val statusButton: Button = findViewById(R.id.btnStatus)
        statusButton.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }

        // BottomNavigationView 설정을 BaseActivity에서 처리
        setupBottomNavigation()
    }
}
