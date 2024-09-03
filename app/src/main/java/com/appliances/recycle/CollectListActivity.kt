package com.appliances.recycle

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

data class CollectItem(val imageResId: Int, val name: String, val detail: String, val status: String)
class CollectListActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_list)

        container = findViewById(R.id.container)

        val items = listOf(
            CollectItem(R.drawable.ram, "냉장고", "내용: 제품이 고장났습니다.", "처리중"),
            CollectItem(R.drawable.ram, "그래픽카드", "내용: 그래픽카드 이상.", "수거중"),
            CollectItem(R.drawable.ram, "세탁기", "내용: 세탁기가 작동하지 않습니다.", "수거완료")
        )

        for (item in items) {
            addItemView(item)
        }
    }

    private fun addItemView(item: CollectItem) {
        val itemView = LayoutInflater.from(this).inflate(R.layout.item_collect_list, container, false)

        val imageView: ImageView = itemView.findViewById(R.id.item_image)
        val nameView: TextView = itemView.findViewById(R.id.item_name)
        val detailView: TextView = itemView.findViewById(R.id.item_detail)
        val statusView: TextView = itemView.findViewById(R.id.item_status)

        imageView.setImageResource(item.imageResId)
        nameView.text = item.name
        detailView.text = item.detail
        statusView.text = item.status

        container.addView(itemView)
    }
}