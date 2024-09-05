package com.appliances.recycle

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProductListActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout

    data class ProductItem(val imageResId: Int, val name: String, val detail: String, val status: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_list)

        container = findViewById(R.id.container)

        val items = listOf(
            ProductItem(R.drawable.ram, "냉장고", "내용: 제품이 고장났습니다.", "처리중"),
            ProductItem(R.drawable.ram, "그래픽카드", "내용: 그래픽카드 이상.", "수거중"),
            ProductItem(R.drawable.ram, "세탁기", "내용: 세탁기가 작동하지 않습니다.", "수거완료")
        )

        for (item in items) {
            addItemView(item)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.product_list)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addItemView(item: ProductItem) {
        val itemView =
            LayoutInflater.from(this).inflate(R.layout.item_collect_list, container, false)

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
