package com.bboost.brainboost.ui.topics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bboost.brainboost.databinding.ItemTopicBinding
import com.bboost.brainboost.dto.TopicDto

class TopicAdapter(
    private val data: MutableList<TopicDto>,
    private val onClick: (TopicDto) -> Unit
) : RecyclerView.Adapter<TopicAdapter.VH>() {

    inner class VH(val binding: ItemTopicBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TopicDto) {
            binding.tvTopicName.text = item.name
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(data[position])

    fun setData(newData: List<TopicDto>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
}
