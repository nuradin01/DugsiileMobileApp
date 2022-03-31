package com.dugsiile.dugsiile.ui


import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentLoginBinding
import com.dugsiile.dugsiile.models.EmailAndPassword
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var dialog : Dialog? = null

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.etPassword.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                binding.btnSignin.isEnabled = text.length >= 6
            }
        }

        binding.btnSignin.setOnClickListener{
            validateInputs()
        }
        binding.tvSignup.setOnClickListener {
            val action =
                LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun validateInputs() {
        val email = binding.etEmail.text?.trim().toString().lowercase(Locale.getDefault())
        val password = binding.etPassword.text.toString()
        val myEmailAndPassword = EmailAndPassword(email, password)

        when {
            myEmailAndPassword.email.isEmpty() -> {
                binding.emailInputLayout.error = "Please enter your email"
            }
            !Patterns.EMAIL_ADDRESS.matcher(myEmailAndPassword.email).matches() -> {
                binding.emailInputLayout.error = "Please enter correct email"
            }
            myEmailAndPassword.password.isEmpty() -> {
                binding.passwordInputLayout.error = "Please enter your password"
            }
            else -> {
                binding.emailInputLayout.error = null
                binding.passwordInputLayout.error = null

                authViewModel.login(myEmailAndPassword)
                handleLoginResponse()
            }
        }
    }

    private fun handleLoginResponse() {
        authViewModel.loginResponse.observe(viewLifecycleOwner) { response ->

            when(response){
                is NetworkResult.Error -> {
                   cancelProgressDialog()
                    Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()

                }
                is NetworkResult.Loading -> {
                    showProgressDialog()
                }
                is NetworkResult.Success -> {
                    authViewModel.saveToken(response.data?.token!!)
                    resetForm()
                    cancelProgressDialog()
                    val action =
                        LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    findNavController().navigate(action)
                }
            }

        }
    }

    private fun resetForm() {
       binding.etPassword.text= null
        binding.etEmail.text = null
    }

    /**
     * Method is used to show the Custom Progress Dialog.
     */
    private fun showProgressDialog() {
        dialog = Dialog(requireContext())

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        dialog?.setContentView(R.layout.custom_loader)

        //Start the dialog and display it on screen.
        dialog?.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    private fun cancelProgressDialog() {
        if (dialog != null) {
            dialog?.dismiss()
            dialog = null
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}