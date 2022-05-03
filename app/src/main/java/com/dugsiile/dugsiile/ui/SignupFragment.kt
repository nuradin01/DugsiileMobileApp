package com.dugsiile.dugsiile.ui

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
import androidx.navigation.fragment.findNavController
import coil.load
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentSignupBinding
import com.dugsiile.dugsiile.models.UserData
import com.dugsiile.dugsiile.util.Constants.Companion.BASE_URL
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.AuthViewModel
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*


@AndroidEntryPoint
class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private var gender: String = "Nothing"
    private  var photo: String = "no-photo.jpg"
    private var dialog : Dialog? = null


    private lateinit var authViewModel: AuthViewModel

    private val uCropContract = object : ActivityResultContract<List<Uri>, Uri>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCrop = UCrop.of(inputUri, outputUri)
                .withAspectRatio(5f, 5f)

            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return UCrop.getOutput(intent!!)!!
        }
    }

    private val getImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val outputUri = File(context?.filesDir, "croppedImage.jpg").toUri()
            val listUri = listOf<Uri>(uri, outputUri)
            cropImage.launch(listUri)
        }

    private val cropImage = registerForActivityResult(uCropContract) { uri ->
        if (uri != null) {
            uploadImage(uri)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        binding.ivSignupPicture.setOnClickListener {
            getImageFromGallery.launch("image/*")
        }

        val genderArray = resources.getStringArray(R.array.Gender)
        val arrayAdepter = ArrayAdapter(requireContext(), R.layout.gender, genderArray)
        binding.genderInputSignup.setAdapter(arrayAdepter)

        binding.genderInputSignup.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
               gender = genderArray[position]
            }


        binding.signupbtn.setOnClickListener{
            validateInputs()
        }


        return binding.root
    }
    private fun uploadImage(uri: Uri) {

        val file = File(uri.path!!)
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, requestBody)

        authViewModel.uploadImage(image)

        authViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->


            when (response) {
                is NetworkResult.Error -> {
                    Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("uploadImage err", response.message.toString())
                }
                is NetworkResult.Loading -> {
                    Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()

                }
                is NetworkResult.Success -> {
                    photo = response.data?.image.toString()
                    Log.d("uploadImage", response.data?.image.toString())
                    binding.ivSignupPicture.load( response.data!!.image) {
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

        val subjectOne = binding.subjectOneInputSignup.text.toString()
        val subjectTwo = binding.subjectTwoInputSignup.text.toString()
        val subjectThree = binding.subjectThreeInputSignup.text.toString()
        val subjectFour = binding.subjectFourInputSignup.text.toString()
        val subjectFive = binding.subjectFiveInputSignup.text.toString()
        val subjectSix = binding.subjectSixInputSignup.text.toString()
        val subjectSeven = binding.subjectSevenInputSignup.text.toString()

        val subjectsArrayFromInput = listOf<String>(subjectOne,subjectTwo,subjectThree,subjectFour,subjectFive,subjectSix,subjectSeven)
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

        when {
            userData.name.isEmpty() -> binding.nameInputLayoutSignup.error = "Name is required"
            userData.email.isEmpty() -> binding.emailInputLayoutSignup.error = "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(userData.email).matches() -> binding.emailInputLayoutSignup.error = "Please enter correct email"

            userData.gender.contains("Nothing") -> binding.genderInputLayoutSignup.error = "Gender is required"
            userData.password!!.isEmpty() -> binding.passwordInputLayoutSignup.error = "Password is required"
            userData.school.isEmpty() -> binding.schoolInputLayoutSignup.error = "School name is required"
            subjectOne.isEmpty() -> binding.subjectOneInputLayoutSignup.error = "Subject name is required"
            password != confirmPassword -> {
                binding.confirmPasswordInputLayoutSignup.error = "Passwords must match"
            }
            password.length < 6 -> binding.passwordInputLayoutSignup.error = "Password must be 6 characters long"
            else ->{
                binding.nameInputLayoutSignup.error = null
                binding.emailInputLayoutSignup.error = null
                binding.passwordInputLayoutSignup.error = null
                binding.genderInputLayoutSignup.error = null
                binding.schoolInputLayoutSignup.error = null
                binding.subjectOneInputLayoutSignup.error = null

                authViewModel.signUp(userData)
                handleSignUpResponse()
            }
        }
    }

    private fun handleSignUpResponse() {
        authViewModel.signUpResponse.observe(viewLifecycleOwner) { response ->

            when(response){
                is NetworkResult.Error -> {
                    cancelProgressDialog()
                    if (response.message.toString().contains("The Email you entered is taken by an other account")){
                        binding.emailInputLayoutSignup.error = "This Email is taken by an other account"
                    }
                    Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()

                }
                is NetworkResult.Loading -> {
                    showProgressDialog()
                }
                is NetworkResult.Success -> {
                    authViewModel.saveToken(response.data?.token!!)
                    Thread.sleep(200)
                    cancelProgressDialog()
                    val action = SignupFragmentDirections.actionSignupFragmentToHomeFragment()
                    findNavController().navigate(action)
                }
            }

        }
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