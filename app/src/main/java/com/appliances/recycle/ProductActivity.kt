package com.appliances.recycle

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Button
import android.widget.Toast

class ProductActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        // 툴바와 바텀 네비게이션 설정
        setupToolbar()
        setupBottomNavigation()

        // 버튼에 리스너 추가
        val washingMachineButton: ImageButton = findViewById(R.id.btn_washing_machine)
        washingMachineButton.setOnClickListener {
            Toast.makeText(this, "세탁기 선택", Toast.LENGTH_SHORT).show()
        }

        val cancelButton: Button = findViewById(R.id.btn_cancel)
        cancelButton.setOnClickListener {
            Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show()
        }
    }
}
