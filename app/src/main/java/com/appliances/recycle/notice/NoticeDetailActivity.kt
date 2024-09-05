package com.appliances.recycle.notice

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.appliances.recycle.R
import com.appliances.recycle.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        // 뒤로 가기 버튼 동작
        toolbar.setNavigationOnClickListener {
            finish()  // 현재 액티비티 종료
        }

        val nno = intent.getLongExtra("nno", -1)
        val noticeTitle = findViewById<TextView>(R.id.noticeTitle)
        val noticeContent = findViewById<TextView>(R.id.noticeContent)

        if (nno != -1L) {
            RetrofitInstance.api.getNoticeDetail(nno).enqueue(object : Callback<Notice> {
                override fun onResponse(call: Call<Notice>, response: Response<Notice>) {
                    if (response.isSuccessful) {
                        val notice = response.body()
                        notice?.let {
                            noticeTitle.text = it.ntitle
                            noticeContent.text = it.ncomment
                        }
                    } else {
                        Toast.makeText(this@NoticeDetailActivity, "Failed to retrieve notice", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Notice>, t: Throwable) {
                    Toast.makeText(this@NoticeDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
