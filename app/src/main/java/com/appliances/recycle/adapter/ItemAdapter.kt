package com.appliances.recycle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appliances.recycle.R
import com.appliances.recycle.dto.ItemDTO
import com.bumptech.glide.Glide

class ItemAdapter(private val items: MutableList<ItemDTO>, private val onDeleteClick: (ItemDTO) -> Unit) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.productImage)
        val itemName: TextView = view.findViewById(R.id.productName)
        val itemPrice: TextView = view.findViewById(R.id.productPrice)
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

        // 이미지 로드 (Glide 사용)
        Glide.with(holder.imageView.context)
            .load(item.imageUrl) // item.imageUrl: 아이템 이미지의 URL
            .into(holder.imageView)

        // 삭제 버튼 클릭 리스너
        holder.deleteButton.setOnClickListener {
            onDeleteClick(item) // 아이템 삭제 함수 호출
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun submitList(newItems: List<ItemDTO>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
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
