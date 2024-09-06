package com.appliances.recycle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.appliances.recycle.notice.NoticeListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // 툴바 설정 메서드
    protected fun setupToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)

        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        } else {
            // 로그 출력 또는 툴바가 없다는 것을 처리할 수 있습니다.
        }
    }

    // BottomNavigationView 설정 메서드
    protected fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_pickup -> {
                    val intent = Intent(this, ProductListActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_alert -> {
                    val intent = Intent(this, NoticeListActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
