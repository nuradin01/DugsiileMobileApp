package com.dugsiile.dugsiile.ui

import android.app.ProgressDialog.show
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.adapters.FeeAdapter
import com.dugsiile.dugsiile.databinding.FragmentStudentDetailsBinding
import com.dugsiile.dugsiile.models.AmountFee
import com.dugsiile.dugsiile.models.FeeData
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.nio.file.Files.delete
import java.text.SimpleDateFormat
import java.util.HashMap
import kotlin.math.roundToInt


public class StudentDetailsFragment : Fragment() {
    private val args by navArgs<StudentDetailsFragmentArgs>()
    private lateinit var mainViewModel: MainViewModel
    private var token: String? = null
    private lateinit var feeList: MutableList<FeeData>

    private  val  mAdapter by lazy { FeeAdapter(mainViewModel, object :FeeAdapter.OptionsMenuClickListener{
        override fun onOptionsMenuClicked(position: Int) {
            performOptionsMenuClick(position)
        }
    })}


    private var _binding: FragmentStudentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStudentDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.student = args.student

        mainViewModel.readToken.asLiveData().observe(viewLifecycleOwner) { value ->
            token = value
            if (!token.isNullOrEmpty()) {
                mAdapter.getToken(token!!)
            }
        }
        mAdapter.getLifecycleOwner(viewLifecycleOwner)

        setHasOptionsMenu(true)

        // format joined date
       val JoindedAt = SimpleDateFormat("d/M/yyyy").format(args.student.joinedAt!!)
        binding.tvJoinedDateDetails.text = JoindedAt.toString()

        if(args.student.isScholarship == true) {
            binding.tvFeeDetails.text = "Scholarship"
        } else {
            binding.tvFeeDetails.text = "$${args.student.fee.toString()}"
        }

         feeList= args.student.fees!!.toMutableList()

        if (feeList.isNotEmpty()){
            setupRecyclerView()
            mAdapter.setData(feeList.toList())
        } else {
            binding.tvUnpaidFees.visibility = View.GONE
            binding.unpaidFeesRecyclerview.visibility = View.GONE
        }



        return binding.root
    }

    private fun setupRecyclerView() {
        binding.tvUnpaidFees.visibility = View.VISIBLE
        binding.unpaidFeesRecyclerview.visibility = View.VISIBLE
        binding.unpaidFeesRecyclerview.adapter = mAdapter
        binding.unpaidFeesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.student_details_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.miChargeStudent).isVisible = !args.student.isScholarship!!
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miDeleteStudent -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_round_delete)
                    .setTitle("Delete ${args.student.name}?")

                    .setMessage("Are you sure to delete ${args.student.name}?")

                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Yes") { _, _ ->
                        // Respond to positive button press
                        mainViewModel.deleteStudent("Bearer $token", args.student._id!!)
                        handleDeleteStudentResponse()
                    }
                    .show()

            }
            R.id.miUpdateStudent -> {
                val action =
                StudentDetailsFragmentDirections.actionStudentDetailsFragmentToUpdateStudentFragment(args.student)
                findNavController().navigate(action)
            }
            R.id.miChargeStudent -> {
                MaterialAlertDialogBuilder(requireContext())

                    .setIcon(R.drawable.ic_dollar)
                    .setTitle("Charge Fee")
                    .setView(R.layout.edit_text)

                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Yes") { dialog, _ ->
                        val input =
                            (dialog as AlertDialog).findViewById<TextView>(android.R.id.text1)
                        if (input?.text.isNullOrEmpty() || input?.text.toString() == "0") {
                            Toast.makeText(context, "Please enter fee", Toast.LENGTH_LONG).show()
                        }else {
                           val inputData = input!!.text.toString()
                            val amountCharged = AmountFee(inputData.toFloat())

                            mainViewModel.chargeStudent(
                                "Bearer $token",
                                args.student._id!!,
                                amountCharged
                            )
                            handleChargeStudentResponse()
                        }
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleDeleteStudentResponse() {
        mainViewModel.deleteStudentsResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Error -> {

                    Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()

                }
                is NetworkResult.Success -> {
                    Snackbar.make(binding.root, "Successfully deleted", Snackbar.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                }
            }

        }
    }
    private fun handleChargeStudentResponse() {
        mainViewModel.chargeStudentResponse.observe(viewLifecycleOwner) {response ->
            when (response) {
                is NetworkResult.Success -> {
                    feeList.add(response.data!!.data)
                    val feeSet = feeList.toSet()
                    setupRecyclerView()
                    mAdapter.setData(feeSet.toList())
                    Snackbar.make(binding.root, "$${response.data.data.amountCharged} was charged", Snackbar.LENGTH_SHORT).show()
                    Log.d("charge single student", response.data.toString())
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }
    }

    // this method will handle the onclick options click
    private fun performOptionsMenuClick(position: Int) {
        // create object of PopupMenu and pass context and view where we want
        // to show the popup menu
        val popupMenu = PopupMenu(context , binding.unpaidFeesRecyclerview[position].findViewById(R.id.textViewOptions))
        // add the menu
        popupMenu.inflate(R.menu.fee_menu)
        // implement on menu item click Listener
        popupMenu.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.deleteFee -> {
                    Log.d("deletefee", position.toString())

                    mainViewModel.deleteFee("Bearer $token", feeList[position].id)
                    mainViewModel.deleteFeeResponse.observe(viewLifecycleOwner) { response ->

                        when (response) {
                            is NetworkResult.Success -> {
                                mainViewModel.getFees("Bearer $token", applyQueries())
                                handleFeeResponse()
                                Log.d("deletefee", response.message.toString())
                                Snackbar.make(
                                    binding.materialCardView,
                                    "${feeList[position].amountCharged} was deleted",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            is NetworkResult.Error -> {
                                Toast.makeText(
                                   context,
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                    }
                }

            }
            false
        }
        popupMenu.show()
    }
    private fun handleFeeResponse() {
        mainViewModel.getFeesResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Error -> {

                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Success -> {
                    val fees = mutableListOf<FeeData>()
                    response.data?.data?.forEach {
                        fees.add(it)
                    }
                    mAdapter.setData(fees.toList())
                    Log.d("studentFee",response.data?.count.toString())

                }
            }

        }

    }



    private fun applyQueries(): HashMap<String, String> {
        val queries : HashMap<String, String> = HashMap()
        queries["student"] = args.student._id.toString()
        queries["balance[gt]"] = "0"
        return queries
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}