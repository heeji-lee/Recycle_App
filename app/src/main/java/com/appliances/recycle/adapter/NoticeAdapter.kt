package com.appliances.recycle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appliances.recycle.R
import com.appliances.recycle.dto.Notice

class NoticeAdapter(
    private var notices: List<Notice>,
    private val onItemClick: (Notice) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notice, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = notices[position]
        holder.bind(notice)

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClick(notice)
        }
    }

    override fun getItemCount(): Int {
        return notices.size
    }

    // 공지 목록 반환하는 함수 추가
    fun getNotices(): List<Notice> {
        return notices
    }

    fun updateNotices(newNotices: List<Notice>) {
        this.notices = newNotices
        notifyDataSetChanged()
    }

    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val noticeTitle: TextView = itemView.findViewById(R.id.noticeTitle)

        fun bind(notice: Notice) {
            noticeTitle.text = notice.ntitle
        }
    }
}
