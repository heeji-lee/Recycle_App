package com.appliances.recycle

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
private lateinit var textProductName: TextView
    private lateinit var textQuantity: TextView
    private lateinit var textReservationDate: TextView
    private lateinit var textAddress: TextView
    private lateinit var textRequestDetails: TextView
    private lateinit var btnEditDate: Button
    private lateinit var btnEditCollectionInfo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_detail)

        // View 초기화
        textProductName = findViewById(R.id.text_product_name)
        textQuantity = findViewById(R.id.text_quantity)
        textReservationDate = findViewById(R.id.text_reservation_date)
        textAddress = findViewById(R.id.text_address)
        textRequestDetails = findViewById(R.id.text_request_details)
        btnEditDate = findViewById(R.id.btn_edit_date)
        btnEditCollectionInfo = findViewById(R.id.btn_edit_collection_info)

        // DB에서 값 가져오기 (여기서는 예제로 하드코딩)
        textProductName.text = "스마트폰"
        textQuantity.text = "1개"
        textReservationDate.text = "2024-09-15"
        textAddress.text = "서울시 강남구 테헤란로 123"
        textRequestDetails.text = "방문 전 미리 연락 바랍니다."

        // 예약 예정일 수정 버튼 클릭 이벤트
        btnEditDate.setOnClickListener {
            editText(textReservationDate)
        }

        // 수거 정보 수정 버튼 클릭 이벤트
        btnEditCollectionInfo.setOnClickListener {
            editText(textAddress)
            editText(textRequestDetails)
        }
    }

    private fun editText(textView: TextView) {
        val editText = EditText(this)
        editText.setText(textView.text)
        textView.visibility = View.GONE
        val parent = textView.parent as LinearLayout
        parent.addView(editText, parent.indexOfChild(textView))

        // 편집 완료 후 텍스트 업데이트
        editText.setOnEditorActionListener { v, actionId, event ->
            textView.text = editText.text
            textView.visibility = View.VISIBLE
            parent.removeView(editText)
            true
        }
    }
}