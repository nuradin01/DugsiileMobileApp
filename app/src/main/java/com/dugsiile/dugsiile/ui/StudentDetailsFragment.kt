package com.dugsiile.dugsiile.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.adapters.FeeAdapter
import com.dugsiile.dugsiile.adapters.StudentAdapter
import com.dugsiile.dugsiile.databinding.FragmentStudentDetailsBinding
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class StudentDetailsFragment : Fragment() {
    private val args by navArgs<StudentDetailsFragmentArgs>()
    private lateinit var mainViewModel: MainViewModel
    private var token: String? = null

    private val mAdapter by lazy { FeeAdapter() }


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

        }

        setHasOptionsMenu(true)

        // format joined date
       val JoindedAt = SimpleDateFormat("d/M/yyyy").format(args.student.joinedAt!!)
        binding.tvJoinedDateDetails.text = JoindedAt.toString()

        if(args.student.isScholarship == true) {
            binding.tvFeeDetails.text = "$0"
        } else {
            binding.tvFeeDetails.text = "$${args.student.fee.toString()}"
        }
        if (args.student.fees!!.isNotEmpty()){
            setupRecyclerView()
            mAdapter.setData(args.student.fees!!)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miDeleteStudent -> {
                MaterialAlertDialogBuilder(requireContext())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}