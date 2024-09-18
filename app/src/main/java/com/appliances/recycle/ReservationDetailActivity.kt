package com.appliances.recycle

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appliances.recycle.dto.MemberDTO
import com.appliances.recycle.retrofit.INetworkService
import com.appliances.recycle.retrofit.MyApplication
import com.appliances.recycle.viewModel.LoginViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class ReservationDetailActivity : AppCompatActivity() {

    private lateinit var networkService: INetworkService

    private lateinit var textCollectionDate: TextView
    private lateinit var textMemberName: TextView
    private lateinit var textMemberPhone: TextView
    private lateinit var textAddress: TextView
    private lateinit var editMemberName: EditText
    private lateinit var editMemberPhone: EditText
    private lateinit var editAddress: EditText
    private lateinit var btnSelectDate: Button
    private lateinit var btnEditInfo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_detail)

        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)
        networkService = myApplication.getApiService()

        // View 초기화
        textCollectionDate = findViewById(R.id.collection_date)
        textMemberName = findViewById(R.id.member_name)
        textMemberPhone = findViewById(R.id.member_phone)
        textAddress = findViewById(R.id.member_address)
        editMemberName = findViewById(R.id.edit_member_name)
        editMemberPhone = findViewById(R.id.edit_member_phone)
        editAddress = findViewById(R.id.edit_address)
        btnSelectDate = findViewById(R.id.btn_select_date)
        btnEditInfo = findViewById(R.id.btn_edit_info)

        // DB에서 값 가져오기 (여기서는 예제로 하드코딩)
//        textCollectionDate.text =
//        textMemberName =
//        textAddress.text =
//        textMemberPhone =

        // 예약 예정일 수정 버튼 클릭 이벤트
        btnSelectDate.setOnClickListener {
            // 오늘 날짜를 기준으로 시작
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // DatePickerDialog 생성
            val datePickerDialog = DatePickerDialog(
                this,
                { view, selectedYear, selectedMonth, selectedDay ->
                    // 날짜 선택 후 처리 (선택된 날짜를 텍스트뷰나 변수에 저장)
                    val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    // 선택된 날짜를 처리할 수 있도록 추가 로직 작성
                    // 예: 텍스트뷰에 선택된 날짜 표시
                    textCollectionDate.text = selectedDate
                },
                year, month, day
            )

            // 1년 내에서만 선택 가능하도록 범위 설정
            val oneYearLater = Calendar.getInstance()
            oneYearLater.add(Calendar.YEAR, 1)
            datePickerDialog.datePicker.minDate = calendar.timeInMillis // 오늘 날짜부터
            datePickerDialog.datePicker.maxDate = oneYearLater.timeInMillis // 1년 후까지

            // DatePickerDialog 보여주기
            datePickerDialog.show()
        }

        editMemberPhone.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false
            private var prevLength: Int = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                prevLength = s?.length ?: 0
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 아무 동작하지 않음
            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true

                s?.let {
                    val digitsOnly = s.toString().replace(Regex("[^\\d]"), "") // 숫자만 필터링
                    val formattedNumber = formatPhoneNumber(digitsOnly) // 포맷 적용
                    s.replace(0, s.length, formattedNumber) // 포맷된 번호로 대체
                }

                isFormatting = false
            }

            // 전화번호 포맷 적용 (000-0000-0000 형식)
            private fun formatPhoneNumber(digits: String): String {
                return when {
                    digits.length <= 3 -> digits // 3자리 이하일 때는 그대로 출력
                    digits.length <= 7 -> "${digits.substring(0, 3)}-${digits.substring(3)}" // 000-0000
                    else -> "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7)}" // 000-0000-0000
                }
            }
        })

        // 정보 수정 버튼 클릭 이벤트
        btnEditInfo.setOnClickListener {
            // TextView 숨기기
            textMemberName.visibility = View.GONE
            textAddress.visibility = View.GONE
            textMemberPhone.visibility = View.GONE

            // EditText 보이기
            editMemberName.visibility = View.VISIBLE
            editAddress.visibility = View.VISIBLE
            editMemberPhone.visibility = View.VISIBLE

            // 기존 TextView의 값을 EditText에 복사
            editMemberName.setText(textMemberName.text)
            editAddress.setText(textAddress.text)
            editMemberPhone.setText(textMemberPhone.text)

            networkService.getOrders().enqueue(object : Callback<MemberDTO> {
                override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                    if (response.isSuccessful) {
                        val member = response.body()
                        if (member != null) {
                            Log.d("API", "User Info: ${member.mname}, ${member.address}, ${member.phone}")

                            // UI 업데이트 (예시)
                            textMemberName.text = member.mname ?: "이름 없음"
                            textAddress.text = member.address ?: "주소 없음"
                            textMemberPhone.text = member.phone ?: "전화번호 없음"
                        } else {
                            Log.e("API", "Received empty body")
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Log.e("API", "Failed to retrieve data: $errorMessage")
                        when (response.code()) {
                            401 -> Log.e("API", "Unauthorized: 로그인 필요")
                            404 -> Log.e("API", "User not found")
                            else -> Log.e("API", "Unexpected error: ${response.code()}")
                        }
                    }
                }

                override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                    Log.e("API", "Network request failed: ${t.message}")
                }
            })
        }

    }

}