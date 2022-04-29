package com.dugsiile.dugsiile.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.dugsiile.dugsiile.databinding.FragmentFeesBinding
import com.dugsiile.dugsiile.models.FeeData
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FeesFragment : Fragment() {

    private var _binding: FragmentFeesBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private var token: String? = null

    private lateinit var calendar: Calendar
    private  var dayOfMonth: Int =1
    private  var firstDayOfMonthInMills : Long =0
    private  var defaultStart : Long? =null
    private  var defaultEnd : Long? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFeesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        calendar = Calendar.getInstance()
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        firstDayOfMonthInMills = (calendar.timeInMillis - (dayOfMonth.toLong()*24L*60L*60L*1000L))
        defaultStart=firstDayOfMonthInMills
        defaultEnd= calendar.timeInMillis

        mainViewModel.readToken.asLiveData().observe(viewLifecycleOwner) { value ->
            token = value

            if (!token.isNullOrEmpty()) {
                mainViewModel.getFees("Bearer $token", applyQueries())
               handleFeeResponse()
            }

        }
        binding.tvChoose.text = SimpleDateFormat("MMMM").format(calendar.time)

        val constraints: CalendarConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

        binding.tvChoose.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker().setCalendarConstraints(constraints)
                .setSelection(Pair.create(defaultStart,defaultEnd))
                .build()
            datePicker.show(parentFragmentManager, "DatePicker")
            // Setting up the event for when ok is clicked
            datePicker.addOnPositiveButtonClickListener {

                binding.tvChoose.text= datePicker.headerText
                Log.d("timestamp first", datePicker.selection?.first.toString())
                Log.d("timestamp second", datePicker.selection?.second.toString())
                defaultStart = datePicker.selection?.first
                defaultEnd = datePicker.selection?.second
                mainViewModel.getFees("Bearer $token", applyQueries())
                handleFeeResponse()
            }


        }

        return binding.root
    }

    private fun handleFeeResponse() {
        mainViewModel.getFeesResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Error -> {
                    Log.d("get fees error", response.message.toString())
                    binding.ivImageError.visibility = View.VISIBLE
                    binding.tvFeeError.visibility = View.VISIBLE
                    binding.progressBarLoading.visibility=  View.GONE
                    binding.progressCardView.visibility = View.GONE
                    binding.cashInHandCard.visibility= View.GONE
                    binding.remainingFeeCard.visibility= View.GONE
                }
                is NetworkResult.Loading -> {
                    binding.progressBarLoading.visibility=  View.VISIBLE
                }
                is NetworkResult.Success -> {
                    binding.progressCardView.visibility = View.VISIBLE
                    binding.cashInHandCard.visibility= View.VISIBLE
                    binding.remainingFeeCard.visibility= View.VISIBLE
                    binding.progressBarLoading.visibility=  View.GONE
                    binding.ivImageError.visibility = View.GONE
                    binding.tvFeeError.visibility = View.GONE

                        var totalFee  =0f
                        var cashInHand  =0f
                    response.data?.data?.forEach {
                        totalFee += it.amountCharged
                    }
                    val paidFees: List<FeeData> = response.data!!.data.filter { it.isPaid }
                    paidFees.forEach {
                        cashInHand += it.amountPaid!!
                    }
                    val remainingFees: Float = totalFee-cashInHand
                    if (totalFee ==0.0f) {
                        Toast.makeText(requireContext(), "There is no data to show in this range", Toast.LENGTH_LONG).show()
                    } else {
                        binding.tvCashInHand.text = "$${cashInHand.toString()}"
                        binding.tvRemainingFee.text = "$${remainingFees.toString()}"
                        binding.progressBar.progress = ((cashInHand/remainingFees)*100).toInt()
                        binding.tvPercentage.text = "${(cashInHand/remainingFees)*100}%"
                    }

                  Log.d("total fees", totalFee.toString())
                  Log.d("cash in hand", cashInHand.toString())
                  Log.d("remaining fee", remainingFees.toString())

                }
            }

        }

    }

    private fun applyQueries(): HashMap<String,String> {
        val queries : HashMap<String, String> = HashMap()
        queries["chargedAt[gt]"] = defaultStart.toString()
        queries["chargedAt[lte]"] = defaultEnd.toString()
        return queries
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}