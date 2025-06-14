package com.soze.studio9910

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(
    private val onDateClick: (Int) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var days = listOf<CalendarDay>()

    fun updateDays(newDays: List<CalendarDay>) {
        days = newDays
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int = days.size

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        private val diaryIndicator: View = itemView.findViewById(R.id.diaryIndicator)
        private val selectedBackground: View = itemView.findViewById(R.id.selectedBackground)

        fun bind(calendarDay: CalendarDay) {
            tvDay.text = calendarDay.day.toString()

            // 현재 월이 아닌 날짜는 회색으로 표시
            if (calendarDay.isCurrentMonth) {
                tvDay.setTextColor(itemView.context.getColor(R.color.black))
                tvDay.alpha = 1.0f
            } else {
                tvDay.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
                tvDay.alpha = 0.3f
            }

            // 일기 있음 표시
            diaryIndicator.visibility = if (calendarDay.hasDiary && calendarDay.isCurrentMonth) {
                View.VISIBLE
            } else {
                View.GONE
            }

            // 선택된 날짜 배경
            selectedBackground.visibility = if (calendarDay.isSelected) {
                View.VISIBLE
            } else {
                View.GONE
            }

            // 클릭 리스너 (현재 월의 날짜만)
            if (calendarDay.isCurrentMonth) {
                itemView.setOnClickListener {
                    onDateClick(calendarDay.day)
                }
                itemView.isClickable = true
            } else {
                itemView.setOnClickListener(null)
                itemView.isClickable = false
            }
        }
    }
} 