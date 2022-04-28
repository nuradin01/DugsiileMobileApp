package com.dugsiile.dugsiile.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentFeesBinding
import com.dugsiile.dugsiile.databinding.FragmentHomeBinding
import com.dugsiile.dugsiile.models.FeeData
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.internal.bind.util.ISO8601Utils
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.Year
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap
import kotlin.time.Duration.Companion.milliseconds

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
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFeesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

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

        binding.tvChoose.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
            datePicker.show(parentFragmentManager, "DatePicker")

            // Setting up the event for when ok is clicked
            datePicker.addOnPositiveButtonClickListener {
                Toast.makeText(
                    requireContext(),
                    "${datePicker.selection?.first} is selected",
                    Toast.LENGTH_LONG
                ).show()
                binding.tvChoose.text= datePicker.headerText
                Log.d("timestamp first", datePicker.selection?.first.toString())
                Log.d("timestamp second", datePicker.selection?.second.toString())
                defaultStart = datePicker.selection?.first
                defaultEnd = datePicker.selection?.second
                mainViewModel.getFees("Bearer $token", applyQueries())
                handleFeeResponse()
            }

            // Setting up the event for when cancelled is clicked
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(
                    requireContext(),
                    "${datePicker.headerText} is cancelled",
                    Toast.LENGTH_LONG
                ).show()
            }

            // Setting up the event for when back button is pressed
            datePicker.addOnCancelListener {
                Toast.makeText(requireContext(), "Date Picker Cancelled", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    private fun handleFeeResponse() {
        mainViewModel.getFeesResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Error -> {
                    Log.d("get fees error", response.message.toString())

                }
                is NetworkResult.Success -> {
                        var totalFee  =0f
                        var cashInHand  =0f
                        var remainingFees  =0f
                        var paidFees = emptyList<FeeData>()
                    response.data?.data?.forEach {
                        totalFee += it.amountCharged
                    }
                    paidFees= response.data!!.data.filter { it.isPaid }
                    paidFees.forEach {
                        cashInHand += it.amountPaid!!
                    }
                    remainingFees= totalFee-cashInHand

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