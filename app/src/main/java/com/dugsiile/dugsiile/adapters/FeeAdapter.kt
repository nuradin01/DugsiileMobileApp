package com.dugsiile.dugsiile.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FeeRowLayoutBinding
import com.dugsiile.dugsiile.models.FeeData
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.util.StudentsDiffUtil
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class FeeAdapter(
    private val mainViewModel: MainViewModel,
) : RecyclerView.Adapter<FeeAdapter.MyViewHolder>() {

    private var fees = emptyList<FeeData>()
    private var token: String? = null
    private var lifecycleOwner: LifecycleOwner? = null

    class MyViewHolder(val binding: FeeRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(fee: FeeData) {
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
        holder.binding.btnReceive.setOnClickListener {
            MaterialAlertDialogBuilder(holder.binding.btnReceive.context)
                .setIcon(R.drawable.ic_dollar)
                .setTitle("Receive Fee?")

                .setMessage("Are you sure you want to receive $${currentFee.amountCharged}?")

                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { _, _ ->
                    // Respond to positive button press
                    mainViewModel.receivePayment("Bearer $token", currentFee.id)
                    mainViewModel.receivePaymentResponse.observe(lifecycleOwner!!) { response ->

                        when (response) {
                            is NetworkResult.Success -> {


                                Log.d("receive payment", response.message.toString())
                                Snackbar.make(
                                    holder.binding.btnReceive,
                                    "$${response.data?.data?.amountCharged} was received",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                val mutableFees = fees.toMutableList()
                                mutableFees.remove(currentFee)
                                setData(mutableFees.toList())

                            }
                            is NetworkResult.Error -> {
                                Toast.makeText(
                                    holder.binding.btnReceive.context,
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        }
                    }
                }
                .show()
        }

    }


    override fun getItemCount(): Int {
        return fees.size
    }

    fun setData(newData: List<FeeData>) {
        val feesDiffUtil =
            StudentsDiffUtil(fees, newData)
        val diffUtilResult = DiffUtil.calculateDiff(feesDiffUtil)
        fees = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun getToken(tokenFromDetailsFragment: String) {
        token = tokenFromDetailsFragment
    }

    fun getLifecycleOwner(lifecycleOwnerFromDetailsFragment: LifecycleOwner?) {
        lifecycleOwner = lifecycleOwnerFromDetailsFragment
    }
}