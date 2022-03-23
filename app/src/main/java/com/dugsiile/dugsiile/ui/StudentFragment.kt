package com.dugsiile.dugsiile.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentHomeBinding
import com.dugsiile.dugsiile.databinding.FragmentStudentBinding


class StudentFragment : Fragment() {
    private var _binding: FragmentStudentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStudentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this


        return binding.root
    }

}