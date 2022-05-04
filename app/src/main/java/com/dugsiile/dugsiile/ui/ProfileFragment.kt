package com.dugsiile.dugsiile.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.adapters.StudentAdapter
import com.dugsiile.dugsiile.adapters.SubjectsAdapter
import com.dugsiile.dugsiile.databinding.FragmentProfileBinding
import com.dugsiile.dugsiile.util.Constants
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    private var token: String? = null
    private val mAdapter by lazy { SubjectsAdapter() }

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
        binding.lifecycleOwner = viewLifecycleOwner

        mainViewModel.readToken.asLiveData().observe(viewLifecycleOwner) { value ->
            token = value

        if (!token.isNullOrEmpty()) {
            mainViewModel.getLogedinUser("Bearer $token")
            mainViewModel.getStudents("Bearer $token")
            handleLogedinUserResponse()
            handleStudentsResponse()
        }

        }
        binding.subjectsRecyclerView.adapter = mAdapter
        binding.subjectsRecyclerView.layoutManager = GridLayoutManager(context,2)


        return binding.root
    }

    private fun handleLogedinUserResponse() {
        mainViewModel.logedinUser.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Error -> {
                    Log.d("user response error", response.message.toString())
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    lifecycleScope.launch {
                        mainViewModel.readUser.observe(viewLifecycleOwner) { database ->
                            if (!database.isNullOrEmpty()) {
                                binding.ivImageError.visibility = View.GONE
                                binding.tvProfileError.visibility = View.GONE
                                binding.cardView2.visibility = View.VISIBLE
                                binding.tvSubjects.visibility = View.VISIBLE
                                binding.tvStudents.visibility = View.VISIBLE

                                binding.tvName.text = database.first().User.name
                                binding.tvSchool.text = database.first().User.school
                                binding.tvEmail.text = database.first().User.email
                                binding.tvGender.text = database.first().User.gender
                                mAdapter.setData(database.first().User.schoolSubjects)
                            } else {
                                binding.ivImageError.visibility = View.VISIBLE
                                binding.tvProfileError.visibility = View.VISIBLE
                                binding.cardView2.visibility=View.GONE
                                binding.tvSubjects.visibility = View.GONE
                                if (response.message != "Unable to resolve host " +
                                    "\"dugsiilemobile.herokuapp.com\": No address " +
                                    "associated with hostname") {
                                    binding.tvProfileError.text = response.message
                                } else {
                                    binding.tvProfileError.text = "Something went wrong."
                                }

                            }
                        }
                    }
//                    binding.ivImageError.visibility = View.VISIBLE
//                    binding.tvProfileError.visibility = View.VISIBLE
//                    if (response.message != "Unable to resolve host " +
//                        "\"dugsiilemobile.herokuapp.com\": No address " +
//                        "associated with hostname") {
//                        binding.tvProfileError.text = response.message
//                    } else {
//                        binding.tvProfileError.text = "Something went wrong."
//                    }
//                    binding.cardView2.visibility=View.GONE
//                    binding.tvSubjects.visibility = View.GONE

                }
                is NetworkResult.Loading -> {
                    binding.progressBar.visibility= View.VISIBLE
                    binding.ivImageError.visibility = View.GONE
                    binding.tvProfileError.visibility = View.GONE
                }
                is NetworkResult.Success -> {
                    binding.cardView2.visibility=View.VISIBLE
                    binding.tvSubjects.visibility = View.VISIBLE

                    binding.ivProfilePicture.load(response.data?.data?.photo) {
                        crossfade(true)
                        error(R.drawable.ic_person_round_white)
                    }
                    binding.tvName.text = response.data?.data?.name
                    binding.tvSchool.text = response.data?.data?.school
                    binding.tvEmail.text = response.data?.data?.email
                    binding.tvGender.text = response.data?.data?.gender
                    mAdapter.setData(response.data!!.data.schoolSubjects)

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