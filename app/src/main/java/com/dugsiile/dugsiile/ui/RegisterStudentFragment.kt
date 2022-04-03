package com.dugsiile.dugsiile.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentHomeBinding
import com.dugsiile.dugsiile.databinding.FragmentStudentRegisterBinding
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterStudentFragment : Fragment() {
    private var _binding: FragmentStudentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    private var token: String? = null
    private var subjects : List<String>? = null
    private var selectedSubjects : ArrayList<Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStudentRegisterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this


        mainViewModel.readToken.asLiveData().observe(viewLifecycleOwner) { value ->
            token = value


        }

        val genderArray = resources.getStringArray(R.array.Gender)
        val arrayAdepter = ArrayAdapter(requireContext(), R.layout.gender, genderArray)
        binding.genderInputStudent.setAdapter(arrayAdepter)





        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}