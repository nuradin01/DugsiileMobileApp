package com.dugsiile.dugsiile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dugsiile.dugsiile.databinding.FeeRowLayoutBinding
import com.dugsiile.dugsiile.databinding.StudentRowLayoutBinding
import com.dugsiile.dugsiile.models.FeeData
import com.dugsiile.dugsiile.models.Student
import com.dugsiile.dugsiile.models.StudentData
import com.dugsiile.dugsiile.util.StudentsDiffUtil

class FeeAdapter: RecyclerView.Adapter<FeeAdapter.MyViewHolder>() {

    private var fees = emptyList<FeeData>()

    class MyViewHolder(private val binding: FeeRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(fee: FeeData){
            binding.fee = fee
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FeeRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentFee = fees[position]
        holder.bind(currentFee)
    }

    override fun getItemCount(): Int {
        return fees.size
    }

    fun setData(newData: List<FeeData>){
        val feesDiffUtil =
            StudentsDiffUtil(fees, newData)
        val diffUtilResult = DiffUtil.calculateDiff(feesDiffUtil)
        fees = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}