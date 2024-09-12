package com.appliances.recycle

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

class MainPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        // 처음 액티비티가 시작될 때 기본 프래그먼트 설정 (홈 화면)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_home, HomeFragment())
                .commit()
        }

        // BottomNavigationView 설정
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_home -> selectedFragment = HomeFragment()  // 홈 프래그먼트
                R.id.navigation_pickup -> selectedFragment = ProductListFragment() // 수거현황 프래그먼트
                R.id.navigation_camera -> selectedFragment = ProductFragment() // 카메라 프래그먼트
                R.id.navigation_alert -> selectedFragment = NoticeListFragment()  // 공지사항 프래그먼트
                // 마이페이지 프래그먼트
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_home, selectedFragment)  // 프래그먼트 컨테이너의 ID 사용
                    .commit()
                true
            } else {
                false
            }
        }

        // 상태바 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = Color.parseColor("#48b8e7")
            }
        }
    }

}
