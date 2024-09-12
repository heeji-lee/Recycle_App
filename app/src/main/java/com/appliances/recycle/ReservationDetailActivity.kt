package com.appliances.recycle

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.appliances.recycle.adapter.ItemAdapter
import com.appliances.recycle.adapter.OrderItemAdapter
import com.appliances.recycle.dto.ItemDTO
import com.google.android.material.datepicker.MaterialDatePicker

class ReservationDetailActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_reservation_detail)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resv)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }

    private lateinit var textReservationDate: TextView
    private lateinit var textAddress: TextView
    private lateinit var btnSelectDate: Button
    private lateinit var btnEditAddress: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_detail)

        // View 초기화
        textReservationDate = findViewById(R.id.reservation_date)
        textAddress = findViewById(R.id.address)
        btnSelectDate = findViewById(R.id.btn_select_date)
        btnEditAddress = findViewById(R.id.btn_edit_address)

        // DB에서 값 가져오기 (여기서는 예제로 하드코딩)
        textReservationDate.text = "2024-09-15"
        textAddress.text = "서울시 강남구 테헤란로 123"

        // 예약 예정일 수정 버튼 클릭 이벤트
        btnSelectDate.setOnClickListener {

        }

        // 수거 정보 수정 버튼 클릭 이벤트
        btnEditAddress.setOnClickListener {

        }
    }

}