package com.dugsiile.dugsiile.ui


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import coil.load
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentStudentRegisterBinding
import com.dugsiile.dugsiile.models.StudentData
import com.dugsiile.dugsiile.util.Constants
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.AuthViewModel
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class RegisterStudentFragment : Fragment() {
    private var _binding: FragmentStudentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel

    private var token: String? = null
    private var gender: String = "Nothing"
    private var photo: String = "no-photo.jpg"
    private var dialog: Dialog? = null


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
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

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

        binding.ivEnrollStudentPicture.setOnClickListener {
            getImageFromGallery.launch("image/*")
        }

        val genderArray = resources.getStringArray(R.array.Gender)
        val arrayAdepter = ArrayAdapter(requireContext(), R.layout.gender, genderArray)
        binding.genderInputStudent.setAdapter(arrayAdepter)

        binding.genderInputStudent.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                gender = genderArray[position]
            }

        binding.btnEnroll.setOnClickListener {
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
                    binding.ivEnrollStudentPicture.load(Constants.BASE_URL + "/" + response.data!!.image) {
                        crossfade(true)
                        error(R.drawable.ic_upload_profile)
                    }

                }
            }

        }
    }

    private fun validateInputs() {
        val studentName = binding.nameInputStudent.text.toString()
        val parentName =
            if (binding.parentInputStudent.text.isNullOrEmpty()) null else binding.parentInputStudent.text.toString()
        val parentPhone =
            if (binding.parentPhoneInputStudent.text.isNullOrEmpty()) null else binding.parentPhoneInputStudent.text.toString()
        val studentPhone =
            if (binding.studentPhoneInputStudent.text.isNullOrEmpty()) null else binding.studentPhoneInputStudent.text.toString()
        val fee =
            if (binding.feeInputStudent.text.isNullOrEmpty()) null else binding.feeInputStudent.text.toString()
        val isScholarship = binding.scholarship.isChecked


        val studentData = StudentData(
            name = studentName,
            fee = fee?.toFloat(),
            gender = gender,
            fees = null,
            photo = photo,
            isLeft = false,
            isScholarship = isScholarship,
            joinedAt = null,
            leftAt = null,
            parentName = parentName,
            parentPhone = parentPhone,
            studentPhone = studentPhone,
            studentSubjects = null,
            user = null,
            _id = null
        )

        when {
            studentData.name.isEmpty() -> binding.nameInputLayoutStudent.error = "Name is required"

            studentData.gender.contains("Nothing") -> binding.genderInputLayoutStudent.error =
                "Gender is required"

            else -> {
                binding.nameInputLayoutStudent.error = null
                binding.genderInputLayoutStudent.error = null


                mainViewModel.addStudent("Bearer $token", studentData)
                handleAddStudentResponse()
            }
        }
    }

    private fun handleAddStudentResponse() {
        mainViewModel.addStudentResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Error -> {
                    cancelProgressDialog()
                    Log.d("Add student", response.message.toString())

                }
                is NetworkResult.Loading -> {
                    showProgressDialog()
                }
                is NetworkResult.Success -> {
                    cancelProgressDialog()

                    Snackbar.make(binding.root, "Enrolled", Snackbar.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}