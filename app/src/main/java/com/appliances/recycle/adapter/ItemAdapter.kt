package com.appliances.recycle.adapter

import android.content.Context
import android.content.SharedPreferences
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ItemAdapter(val items: MutableList<ItemDTO>, private val onDeleteClick: (ItemDTO) -> Unit, private val context: Context // SharedPreferences를 사용하기 위한 Context
) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)

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
    fun submitList(newItems: MutableList<ItemDTO>) {
        newItems.forEach { newItem ->
            items.add(newItem) }
        // 리스트가 변경된 후 SharedPreferences에 저장
        saveItemsToSharedPrefs(items)
        notifyDataSetChanged()
    }
    // SharedPreferences에 리스트 저장
    private fun saveItemsToSharedPrefs(items: List<ItemDTO>) {
        val gson = Gson()
        val jsonString = gson.toJson(items) // 리스트를 JSON 문자열로 변환
        with(sharedPrefs.edit()) {
            putString("items_key", jsonString)
            apply() // SharedPreferences에 저장
        }
    }

    // SharedPreferences에서 리스트를 불러오는 함수
    fun loadItemsFromSharedPrefs(): List<ItemDTO> {
        val jsonString = sharedPrefs.getString("items_key", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<ItemDTO>>() {}.type
            Gson().fromJson(jsonString, type) // JSON 문자열을 리스트로 변환
        } else {
            mutableListOf() // SharedPreferences에 값이 없을 경우 빈 리스트 반환
        }
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