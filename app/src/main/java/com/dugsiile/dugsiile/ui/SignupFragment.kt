package com.dugsiile.dugsiile.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import coil.load
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentSignupBinding
import com.dugsiile.dugsiile.util.Constants.Companion.BASE_URL
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@AndroidEntryPoint
class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    private val uCropContract = object : ActivityResultContract<List<Uri>, Uri>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCrop = UCrop.of(inputUri, outputUri)
                .withAspectRatio(5f,5f)

            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
           return UCrop.getOutput(intent!!)!!
        }
    }

    private val getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()){uri ->
        val outputUri = File(context?.filesDir, "croppedImage.jpg").toUri()
        val listUri = listOf<Uri>(uri, outputUri)
        cropImage.launch(listUri)
    }

    private val cropImage = registerForActivityResult(uCropContract){uri ->
//        binding.ivSignupPicture.load(uri){
//            crossfade(true)
//        }
        if (uri != null) {
            uploadImage(uri)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
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

        val gender = resources.getStringArray(R.array.Gender)
        val arrayAdepter = ArrayAdapter(requireContext(),R.layout.gender, gender)
        binding.genderInputSignup.setAdapter(arrayAdepter)

        binding.genderInputSignup.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
            Toast.makeText(requireContext(),gender[position],Toast.LENGTH_SHORT).show()
            }




        return binding.root
    }

    private fun uploadImage(uri:Uri){

        val file  = File(uri.path!!)
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, requestBody)

        mainViewModel.uploadImage(image)

        mainViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->


            when(response){
                is NetworkResult.Error -> {
                    Toast.makeText(context, response.message.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("uploadImage err", response.message.toString())
                }
                is NetworkResult.Loading -> {
                    Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()

                }
                is NetworkResult.Success -> {
                    Toast.makeText(context, response.data.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("uploadImage", response.data?.image.toString())
                    binding.ivSignupPicture.load(BASE_URL+"/"+response.data!!.image) {
                        crossfade(true)
                        error(R.drawable.ic_upload_profile)
                    }

                }
            }

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