package com.appliances.recycle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appliances.recycle.R
import com.appliances.recycle.dto.ItemDTO

class ItemAdapter(private val items: MutableList<ItemDTO>, private val onDeleteClick: (ItemDTO) -> Unit) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemPrice: TextView = view.findViewById(R.id.item_price)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.iname
        holder.itemPrice.text = item.iprice?.toString() ?: "가격 없음"

        // 삭제 버튼 클릭 리스너
        holder.deleteButton.setOnClickListener {
            onDeleteClick(item) // 아이템 삭제 함수 호출
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // 아이템을 삭제하는 함수
    fun deleteItem(item: ItemDTO) {
        val position = items.indexOf(item)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
