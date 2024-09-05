package com.appliances.recycle

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        // 결제 방법 Buttons
        val buttonCreditCard: Button = findViewById(R.id.btnCreditCard)
        val buttonAccountTransfer: Button = findViewById(R.id.btnAccountTransfer)
        val buttonVirtualAccount: Button = findViewById(R.id.btnVirtualAccount)
        val buttonMobilePayment: Button = findViewById(R.id.btnMobilePayment)

        // 결제 수단 이미지 버튼들
        val nPay: ImageView = findViewById(R.id.nPay)
        val kPay: ImageView = findViewById(R.id.kPay)
        val tossPay: ImageView = findViewById(R.id.tossPay)

        // 결제 버튼
        val paymentButton: Button = findViewById(R.id.paymentButton)

        // 결제 버튼 클릭 이벤트
        paymentButton.setOnClickListener {
            val selectedPaymentMethod = when {
                buttonCreditCard.isPressed -> "신용-체크카드"
                buttonAccountTransfer.isPressed -> "계좌이체"
                buttonVirtualAccount.isPressed -> "가상계좌"
                buttonMobilePayment.isPressed -> "휴대폰"
                else -> "다른 결제 수단"
            }

            // 결제 프로세스 로직 (여기서는 토스트로 간단히 표시)
            Toast.makeText(
                this,
                "$selectedPaymentMethod 을 선택하셨습니다.",
                Toast.LENGTH_LONG
            ).show()
        }

        // 각 결제 수단 이미지 클릭 이벤트 (여기서는 토스트로 간단히 표시)
        nPay.setOnClickListener {
            Toast.makeText(this, "NPay로 결제합니다.", Toast.LENGTH_SHORT).show()
        }
        kPay.setOnClickListener {
            Toast.makeText(this, "KPay로 결제합니다.", Toast.LENGTH_SHORT).show()
        }
        tossPay.setOnClickListener {
            Toast.makeText(this, "TossPay로 결제합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}