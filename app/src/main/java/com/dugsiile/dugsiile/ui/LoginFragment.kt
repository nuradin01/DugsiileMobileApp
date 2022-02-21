package com.dugsiile.dugsiile.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentLoginBinding
import com.dugsiile.dugsiile.models.EmailAndPassword
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.btnSignin.setOnClickListener{
            var email = binding.etEmail.text.toString()
            var password = binding.etPassword.text.toString()
            var myEmailAndPassword = EmailAndPassword(email, password)

            if(email.isNullOrEmpty() ){

                Log.d("Test", "email is empty")
            }
            else if (password.isNullOrEmpty()){
                Log.d("Test", "password is empty")
            }
            else {
                mainViewModel.login(myEmailAndPassword)
                mainViewModel.loginResponse.observe(viewLifecycleOwner) {
                    Log.d("guul", it.data.toString())
                }
            }

        }





        return binding.root
    }
// Hiding the top app bar
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }



}