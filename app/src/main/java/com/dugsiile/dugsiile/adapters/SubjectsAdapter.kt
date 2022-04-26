package com.dugsiile.dugsiile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dugsiile.dugsiile.databinding.StudentRowLayoutBinding
import com.dugsiile.dugsiile.databinding.SubjectsRowLayoutBinding
import com.dugsiile.dugsiile.models.Student
import com.dugsiile.dugsiile.models.StudentData
import com.dugsiile.dugsiile.util.StudentsDiffUtil

class SubjectsAdapter: RecyclerView.Adapter<SubjectsAdapter.MyViewHolder>() {

    private var subjects = emptyList<String>()

    class MyViewHolder(private val binding: SubjectsRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subjects: String){
            binding.subject = subjects
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SubjectsRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentSubject = subjects[position]
        holder.bind(currentSubject)
    }

    override fun getItemCount(): Int {
        return subjects.size
    }

    fun setData(newData: List<String>){
        val studentsDiffUtil =
            StudentsDiffUtil(subjects, newData)
        val diffUtilResult = DiffUtil.calculateDiff(studentsDiffUtil)
        subjects = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}