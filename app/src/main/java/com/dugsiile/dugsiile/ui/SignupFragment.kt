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
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import coil.load
import com.dugsiile.dugsiile.databinding.FragmentSignupBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

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
        binding.ivSignupPicture.load(uri){
            crossfade(true)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

//
        binding.ivSignupPicture.setOnClickListener {
        getImageFromGallery.launch("image/*")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * A companion object to declare the constants.
     */
    companion object {
        //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult
        private const val READ_STORAGE_PERMISSION_CODE = 1

        private const val PICK_IMAGE_REQUEST_CODE = 2
    }
}