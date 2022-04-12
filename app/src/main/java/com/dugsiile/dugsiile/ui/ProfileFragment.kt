package com.dugsiile.dugsiile.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import coil.load
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.databinding.FragmentProfileBinding
import com.dugsiile.dugsiile.util.Constants
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        mainViewModel.readToken.asLiveData().observe(viewLifecycleOwner) { value ->
            token = value

        if (!token.isNullOrEmpty()) {
            mainViewModel.getLogedinUser("Bearer $token")
            mainViewModel.getStudents("Bearer $token")
            handleLogedinUserResponse()
            handleStudentsResponse()
        }

        }


        return binding.root
    }

    private fun handleLogedinUserResponse() {
        mainViewModel.logedinUser.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Error -> {
                    binding.ivImageError.visibility = View.VISIBLE
                    binding.tvProfileError.visibility = View.VISIBLE
                    binding.tvProfileError.text = response.message

                    binding.progressBar.visibility= View.GONE
                    binding.cl2.visibility = View.GONE
                    binding.ivProfilePicture.visibility= View.GONE
                    binding.tvName.visibility = View.GONE
                    binding.tvSchool.visibility = View.GONE
                    binding.cardView2.visibility=View.GONE

                }
                is NetworkResult.Loading -> {
                    binding.progressBar.visibility= View.VISIBLE
                    binding.ivImageError.visibility = View.GONE
                    binding.tvProfileError.visibility = View.GONE
                }
                is NetworkResult.Success -> {
                    binding.cl2.visibility = View.VISIBLE
                    binding.ivProfilePicture.visibility= View.VISIBLE
                    binding.tvName.visibility = View.VISIBLE
                    binding.tvSchool.visibility = View.VISIBLE
                    binding.cardView2.visibility=View.VISIBLE

                    binding.ivProfilePicture.load(Constants.BASE_URL + "/" + response.data?.data?.photo) {
                        crossfade(true)
                        error(R.drawable.ic_person_round_white)
                    }
                    binding.tvName.text = response.data?.data?.name
                    binding.tvSchool.text = response.data?.data?.school

                    binding.progressBar.visibility= View.GONE
                    binding.ivImageError.visibility = View.GONE
                    binding.tvProfileError.visibility = View.GONE
                }
            }

        }

    }

    private fun handleStudentsResponse() {
        mainViewModel.studentsResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Error -> {
                    Log.d("students", response.message.toString())
                    binding.progressBar.visibility= View.GONE
                    binding.tvStudents.visibility=View.GONE
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.visibility= View.VISIBLE

                }
                is NetworkResult.Success -> {
                    binding.tvStudents.visibility=View.VISIBLE
                   binding.tvStudents.text = "${response.data?.count} Students"

                    binding.progressBar.visibility= View.GONE

                }
            }

        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}