package com.appliances.recycle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class ProductListFragment : Fragment() {

    private lateinit var container: LinearLayout

    data class ProductItem(val imageResId: Int, val name: String, val detail: String, val status: String)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃 설정
        val view = inflater.inflate(R.layout.fragment_product_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View 요소 초기화
        container = view.findViewById(R.id.container)

        val items = listOf(
            ProductItem(R.drawable.ram, "냉장고", "내용: 제품이 고장났습니다.", "처리중"),
            ProductItem(R.drawable.ram, "그래픽카드", "내용: 그래픽카드 이상.", "수거중"),
            ProductItem(R.drawable.ram, "세탁기", "내용: 세탁기가 작동하지 않습니다.", "수거완료")
        )

        for (item in items) {
            addItemView(item)
        }

        // 시스템 바를 처리하는 코드
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.product_list)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // 아이템 뷰를 추가하는 함수
    private fun addItemView(item: ProductItem) {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_collect_list, container, false)

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
