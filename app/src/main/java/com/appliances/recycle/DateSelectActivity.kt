package com.appliances.recycle

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateSelectActivity : AppCompatActivity() {

    private lateinit var tvMonthYear: TextView
    private lateinit var calendarView: CalendarView
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_select)

        tvMonthYear = findViewById(R.id.tvMonthYear)
        calendarView = findViewById(R.id.calendarView)
        val btnPrevMonth: ImageButton = findViewById(R.id.btnPrevMonth)
        val btnNextMonth: ImageButton = findViewById(R.id.btnNextMonth)
        val btnCancel: Button = findViewById(R.id.btnCancel)
        val btnSelect: Button = findViewById(R.id.btnSelect)

        updateMonthYearDisplay()

        btnPrevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthYearDisplay()
            updateCalendarView()
        }

        btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthYearDisplay()
            updateCalendarView()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSelect.setOnClickListener {
            val selectedDate = calendarView.date
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormatter.format(selectedDate)

            // 날짜 선택 처리 로직 추가
            Toast.makeText(this, "선택한 날짜: $formattedDate", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMonthYearDisplay() {
        val dateFormatter = SimpleDateFormat("yyyy년 M월", Locale.getDefault())
        tvMonthYear.text = dateFormatter.format(calendar.time)
    }

    private fun updateCalendarView() {
        calendarView.date = calendar.timeInMillis
    }
}