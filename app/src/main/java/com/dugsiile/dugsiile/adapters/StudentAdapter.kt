package com.dugsiile.dugsiile.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dugsiile.dugsiile.databinding.StudentRowLayoutBinding
import com.dugsiile.dugsiile.models.Student
import com.dugsiile.dugsiile.models.StudentData
import com.dugsiile.dugsiile.util.StudentsDiffUtil
import java.util.*

class StudentAdapter: RecyclerView.Adapter<StudentAdapter.MyViewHolder>() {

    private var students = emptyList<StudentData>()
    private var copyStudents = emptyList<StudentData>()
    private var filteredStudents = arrayListOf<StudentData>()

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
        filteredStudents.clear()
        val studentsDiffUtil =
            StudentsDiffUtil(students, newData.data)
        val diffUtilResult = DiffUtil.calculateDiff(studentsDiffUtil)
        students = newData.data
        copyStudents = newData.data
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun filterData(keyword: String){
        filteredStudents.clear()
            copyStudents.forEach {
                if (it.name.lowercase().contains(keyword.lowercase())) {
                    filteredStudents.add(it)
                }
                val studentsDiffUtil =
                    StudentsDiffUtil(students, filteredStudents)
                val diffUtilResult = DiffUtil.calculateDiff(studentsDiffUtil)
                students = filteredStudents.toList()
                diffUtilResult.dispatchUpdatesTo(this)
            }

    }
}