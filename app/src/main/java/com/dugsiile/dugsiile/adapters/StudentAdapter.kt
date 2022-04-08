package com.dugsiile.dugsiile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dugsiile.dugsiile.databinding.StudentRowLayoutBinding
import com.dugsiile.dugsiile.models.Student
import com.dugsiile.dugsiile.models.StudentData
import com.dugsiile.dugsiile.util.StudentsDiffUtil

class StudentAdapter: RecyclerView.Adapter<StudentAdapter.MyViewHolder>() {

    private var students = emptyList<StudentData>()

    class MyViewHolder(private val binding: StudentRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentData){
            binding.student = student
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StudentRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentStudent = students[position]
        holder.bind(currentStudent)
    }

    override fun getItemCount(): Int {
        return students.size
    }

    fun setData(newData: Student){
        val studentsDiffUtil =
            StudentsDiffUtil(students, newData.data)
        val diffUtilResult = DiffUtil.calculateDiff(studentsDiffUtil)
        students = newData.data
        diffUtilResult.dispatchUpdatesTo(this)
    }
}