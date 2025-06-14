package com.soze.studio9910

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class WebtoonCardAdapter(
    private val imageResList: List<Int>
) : RecyclerView.Adapter<WebtoonCardAdapter.WebtoonCardViewHolder>() {

    private var selectedPosition = -1

    inner class WebtoonCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.webtoonImage)
        val stampIcon: ImageView = view.findViewById(R.id.stampIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebtoonCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_webtoon_card, parent, false)
        return WebtoonCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: WebtoonCardViewHolder, position: Int) {
        holder.imageView.setImageResource(imageResList[position])
        
        // 선택된 위치에만 스탬프 아이콘 표시
        if (position == selectedPosition) {
            holder.stampIcon.visibility = View.VISIBLE
        } else {
            holder.stampIcon.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = imageResList.size

    // 선택된 위치 설정
    fun setSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        
        // 이전 선택된 아이템과 새로 선택된 아이템 업데이트
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition)
        }
        notifyItemChanged(position)
    }

    // 현재 선택된 위치 반환
    fun getSelectedPosition(): Int = selectedPosition
} 