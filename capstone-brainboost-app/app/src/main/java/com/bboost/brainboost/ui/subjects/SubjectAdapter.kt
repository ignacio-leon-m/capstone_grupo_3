package com.bboost.brainboost.ui.subjects

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bboost.brainboost.databinding.ItemSubjectBinding
import com.bboost.brainboost.dto.SubjectInfoDto
import com.bboost.brainboost.ui.topics.TopicListActivity

class SubjectAdapter(
    private val items: MutableList<SubjectInfoDto>
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = items[position]

        holder.binding.tvSubjectName.text = subject.name

        holder.itemView.setOnClickListener {
            Log.e("SubjectAdapter", "Launching TopicList with subjectId=${subject.id}")

            val ctx = holder.itemView.context

            val intent = Intent(ctx, TopicListActivity::class.java).apply {
                putExtra(TopicListActivity.EXTRA_SUBJECT_ID, subject.id.toString())
                putExtra(TopicListActivity.EXTRA_SUBJECT_NAME, subject.name)

                // IMPORTANTE: reenviar MODO si viene desde Home
                putExtra("MODE", (ctx as? android.app.Activity)?.intent?.getStringExtra("MODE"))
            }

            ctx.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setData(list: List<SubjectInfoDto>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}
