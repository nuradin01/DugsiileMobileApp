package com.dugsiile.dugsiile.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentHomeBinding
import com.dugsiile.dugsiile.databinding.FragmentLoginBinding
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    private var token : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        mainViewModel.readToken.asLiveData().observe(viewLifecycleOwner) { value ->
            token = value
            binding.tvToken.text = token
            checkIfIsAuthenticated(token)
        }


        return binding.root
    }

    private fun checkIfIsAuthenticated(token: String?) {
if (token == null){
    val action =
        HomeFragmentDirections.actionHomeFragmentToLoginFragment()
    findNavController().navigate(action)
}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}