package com.dugsiile.dugsiile.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import coil.load
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentSignupBinding
import com.dugsiile.dugsiile.models.UserData
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.AuthViewModel
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

@AndroidEntryPoint
class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private var gender: String = "Nothing"
    private var photo: String = "no-photo.jpg"
    private var dialog: Dialog? = null

    private lateinit var authViewModel: AuthViewModel
    private lateinit var mainViewModel: MainViewModel

    // UPDATED: Modernized ActivityResultContract for UCrop
    private val uCropContract = object : ActivityResultContract<List<Uri>, Uri?>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]
            return UCrop.of(inputUri, outputUri)
                .withAspectRatio(1f, 1f) // Standard 1:1 profile crop
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == Activity.RESULT_OK && intent != null) {
                UCrop.getOutput(intent)
            } else null
        }
    }

    private val getImageFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri == null) return@registerForActivityResult
                // Use cacheDir for temporary files to avoid permission issues on SDK 35
                val outputUri = File(requireContext().cacheDir, "temp_crop_${System.currentTimeMillis()}.jpg").toUri()
                cropImage.launch(listOf(uri, outputUri))
            }

    private val cropImage = registerForActivityResult(uCropContract) { uri ->
        uri?.let { uploadImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        binding.ivSignupPicture.setOnClickListener {
            getImageFromGallery.launch("image/*")
        }

        val genderArray = resources.getStringArray(R.array.Gender)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.gender, genderArray)
        binding.genderInputSignup.setAdapter(arrayAdapter)

        binding.genderInputSignup.onItemClickListener =
                OnItemClickListener { _, _, position, _ ->
                    gender = genderArray[position]
                }

        binding.signupbtn.setOnClickListener {
            validateInputs()
        }

        return binding.root
    }

    private fun uploadImage(uri: Uri) {
        // UPDATED: Modern OkHttp 4.x/5.x syntax for SDK 35
        val file = File(uri.path ?: return)
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

        authViewModel.uploadImage(imagePart)

        authViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Error -> {
                    Log.d("uploadImage err", response.message.toString())
                }
                is NetworkResult.Loading -> { /* Show specific small loader if needed */ }
                is NetworkResult.Success -> {
                    photo = response.data?.image.toString()
                    binding.ivSignupPicture.load(response.data!!.image) {
                        crossfade(true)
                        error(R.drawable.ic_upload_profile)
                    }
                }
            }
        }
    }

    private fun validateInputs() {
        val name = binding.nameInputSignup.text.toString()
        val email = binding.emailInputSignup.text.toString().lowercase(Locale.getDefault())
        val password = binding.passwordInputSignup.text.toString()
        val confirmPassword = binding.confirmPasswordInputSignup.text.toString()
        val schoolName = binding.schoolInputSignup.text.toString()

        val subjectsArrayFromInput = listOf(
            binding.subjectOneInputSignup.text.toString(),
            binding.subjectTwoInputSignup.text.toString(),
            binding.subjectThreeInputSignup.text.toString(),
            binding.subjectFourInputSignup.text.toString(),
            binding.subjectFiveInputSignup.text.toString(),
            binding.subjectSixInputSignup.text.toString(),
            binding.subjectSevenInputSignup.text.toString()
        )
        val schoolSubjectsArray = subjectsArrayFromInput.filter { it.isNotEmpty() }

        val userData = UserData(
            name = name,
            email = email,
            password = password,
            gender = gender,
            photo = photo,
            role = null,
            school = schoolName,
            schoolSubjects = schoolSubjectsArray,
            id = null,
            isRegisteredSchool = null,
            joinedAt = null
        )

        // Validation logic
        if (userData.name.isEmpty()) {
            binding.nameInputLayoutSignup.error = "Name is required"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userData.email).matches()) {
            binding.emailInputLayoutSignup.error = "Please enter correct email"
            return
        }
        if (password != confirmPassword) {
            binding.confirmPasswordInputLayoutSignup.error = "Passwords must match"
            return
        }
        if (password.length < 6) {
            binding.passwordInputLayoutSignup.error = "Min 6 characters"
            return
        }

        // Success: Trigger Signup
        binding.nameInputLayoutSignup.error = null
        binding.emailInputLayoutSignup.error = null
        authViewModel.signUp(userData)
        handleSignUpResponse()
    }

    private fun handleSignUpResponse() {
        authViewModel.signUpResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Error -> {
                    cancelProgressDialog()
                    if (response.message.toString().contains("Email", true)) {
                        binding.emailInputLayoutSignup.error = "Email already taken"
                    }
                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> showProgressDialog()
                is NetworkResult.Success -> {
                    authViewModel.saveToken(response.data?.token!!)
                    mainViewModel.readToken.asLiveData().observe(viewLifecycleOwner) { value ->
                        if (!value.isNullOrEmpty()) {
                            cancelProgressDialog()
                            val action = SignupFragmentDirections.actionSignupFragmentToHomeFragment()
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    private fun showProgressDialog() {
        dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.custom_loader)
            setCancelable(false)
            show()
        }
    }

    private fun cancelProgressDialog() {
        dialog?.dismiss()
        dialog = null
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}