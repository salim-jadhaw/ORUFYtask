package com.example.homescreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homescreen.entity.HistoryEntity

class HistoryAdapter(private var historyList: MutableList<HistoryEntity>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUrl: TextView = itemView.findViewById(R.id.tvUrl)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvIndex: TextView = itemView.findViewById(R.id.tvIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]

        // Display index number (starting from 1)
        holder.tvIndex.text = "${position + 1}."

        // Display URL
        holder.tvUrl.text = historyItem.url

        // Display timestamp with label
        holder.tvTime.text = "Visited: ${historyItem.time}"
    }

    override fun getItemCount(): Int = historyList.size

    fun updateList(newList: List<HistoryEntity>) {
        historyList.clear()
        historyList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getHistoryList(): List<HistoryEntity> = historyList.toList()
}