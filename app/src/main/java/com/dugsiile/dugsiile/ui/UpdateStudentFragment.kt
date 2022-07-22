package com.dugsiile.dugsiile.ui

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
import androidx.navigation.fragment.navArgs
import coil.load
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentProfileBinding
import com.dugsiile.dugsiile.databinding.FragmentUpdateStudentBinding
import com.dugsiile.dugsiile.models.StudentData
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
class UpdateStudentFragment : Fragment() {

    private val args by navArgs<UpdateStudentFragmentArgs>()

    private var _binding: FragmentUpdateStudentBinding? = null
    private val binding get() = _binding!!
    private var token: String? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel
    private var gender: String = "Nothing"
    private var photo: String = "no-photo.jpg"


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
        _binding = FragmentUpdateStudentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        mainViewModel.readToken.asLiveData().observe(viewLifecycleOwner) { value ->
            token = value
        }

        fillFields(args)


        binding.ivUpdateStudentPicture.setOnClickListener {
            getImageFromGallery.launch("image/*")
        }
        binding.btnUpdate.setOnClickListener {
            updateStudent()
        }

        val genderArray = resources.getStringArray(R.array.Gender)
        val arrayAdepter = ArrayAdapter(requireContext(), R.layout.gender, genderArray)
        binding.updateGenderInputStudent.setAdapter(arrayAdepter)

        binding.updateGenderInputStudent.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                gender = genderArray[position]
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
                    Log.d("uploadImage", response.data.toString())
                    binding.ivUpdateStudentPicture.load( response.data!!.image) {
                        crossfade(true)
                        error(R.drawable.ic_upload_profile)
                    }

                }
            }

        }
    }

    private fun updateStudent() {
        val fee =   if (binding.updateScholarship.isChecked)
            null
        else binding.updateFeeInputStudent.text.toString()
        if (gender.contains("Nothing")) {
            gender = args.student.gender
        }
        if (photo.contains("no-photo.jpg")){
            photo = args.student.photo!!
        }
        if (binding.updateNameInputStudent.text.isNullOrEmpty()) {

            binding.updateNameInputLayoutStudent.error = "Name is required"
        }  else {
            val studentData = StudentData(
                name = binding.updateNameInputStudent.text.toString(),
                parentName = binding.updateParentInputStudent.text.toString(),
                parentPhone = binding.updateParentPhoneInputStudent.text.toString(),
                studentPhone = binding.updateStudentPhoneInputStudent.text.toString(),
                gender = gender,
                fee = fee?.toFloat(),
                isLeft = false,
                isScholarship = binding.updateScholarship.isChecked,
                joinedAt = null,
                leftAt = null,
                photo = photo,
                user = null,
                _id = null,
                studentSubjects = null,
                fees = null
            )

            mainViewModel.updateStudent("Bearer $token", studentData, args.student._id!!)
            handleUpdateStudentResponse()
        }
    }

    private fun fillFields(args: UpdateStudentFragmentArgs){
        val fee = if (args.student.isScholarship!!) "0" else args.student.fee.toString()
        binding.updateNameInputStudent.setText(args.student.name)
        binding.updateParentInputStudent.setText(args.student.parentName)
        binding.updateParentPhoneInputStudent.setText(args.student.parentPhone)
        binding.updateStudentPhoneInputStudent.setText(args.student.studentPhone)
        binding.updateGenderInputStudent.setText(args.student.gender)
        binding.updateFeeInputStudent.setText(fee)
        binding.updateScholarship.isChecked = args.student.isScholarship!!
        binding.ivUpdateStudentPicture.load(args.student.photo) {
            crossfade(true)
            error(R.drawable.ic_upload_profile)
        }
    }

    private fun handleUpdateStudentResponse() {
        mainViewModel.updateStudentResponse.observe(viewLifecycleOwner){response ->
            when (response) {
                is NetworkResult.Error -> {
                    Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("update student err", response.message.toString())
                }
                is NetworkResult.Success -> {
                    Snackbar.make(binding.root, "Successfully updated", Snackbar.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack(R.id.homeFragment, false)
                }

            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}