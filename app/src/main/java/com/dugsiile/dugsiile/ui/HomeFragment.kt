package com.dugsiile.dugsiile.ui

import android.icu.text.CaseMap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dugsiile.dugsiile.R
import com.dugsiile.dugsiile.adapters.StudentAdapter
import com.dugsiile.dugsiile.databinding.FragmentHomeBinding
import com.dugsiile.dugsiile.util.NetworkResult
import com.dugsiile.dugsiile.viewmodels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    private var token: String? = null
    private val mAdapter by lazy { StudentAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        mainViewModel.readToken.asLiveData().observe(viewLifecycleOwner) { value ->
            token = value

            checkIfIsAuthenticated(token)
            if (!token.isNullOrEmpty()) {
                requestApiData()
            }
            }


        setHasOptionsMenu(true)

        setupRecyclerView()



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun setupRecyclerView() {
        binding.recyclerview.adapter = mAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun requestApiData() {
        Log.d("homeFragment", "requestApiData called!")
        mainViewModel.getStudents("Bearer $token")
        mainViewModel.studentsResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.recyclerview.visibility = View.VISIBLE
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    lifecycleScope.launch {
                        mainViewModel.readStudents.observe(viewLifecycleOwner) { database ->
                            if (!database.isNullOrEmpty()) {
                                mAdapter.setData(database.first().student)
                                binding.errorImageView.visibility = View.GONE
                                binding.errorTextView.visibility = View.GONE
                            } else {
                                binding.errorImageView.visibility = View.VISIBLE
                                binding.errorTextView.visibility = View.VISIBLE
                                if (response.message != "Unable to resolve host " +
                                    "\"dugsiilemobile.herokuapp.com\": No address " +
                                    "associated with hostname") {
                                binding.errorTextView.text = response.message
                                } else {
                                    binding.errorTextView.text = "Something went wrong."
                                }
                            }
                        }
                    }
                    hideShimmerEffect()
                    Log.d("students response error", response.message.toString())
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    binding.errorImageView.visibility = View.GONE
                    binding.errorTextView.visibility = View.GONE
                    binding.recyclerview.visibility = View.VISIBLE
//                    showShimmerEffect()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miSignout -> {
                mainViewModel.signout()
                mainViewModel.clearStudents()
                mainViewModel.clearUser()
            }
            R.id.miAddStudent -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToRegisterStudentFragment()
                findNavController().navigate(action)
            }
            R.id.miChargeAll -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_dollar)
                    .setTitle("Charge Monthly Fee?")

                    .setMessage("Are you sure you want to charge all paying students?")

                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Yes") { _, _ ->

                mainViewModel.chargeAllPaidStudents("Bearer $token")
                handleChargeAllPaidStudentsResponse()
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkIfIsAuthenticated(token: String?) {
        if (token == null) {
            val action =
                HomeFragmentDirections.actionHomeFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }
    private fun handleChargeAllPaidStudentsResponse(){
        mainViewModel.chargeAllResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    requestApiData()
                    Snackbar.make(binding.root, "${response.data!!.count} students charged", Snackbar.LENGTH_SHORT).show()
                    Log.d("charge all", response.data?.count.toString())
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.startShimmer()
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.recyclerview.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.recyclerview.visibility = View.VISIBLE
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
       if (!newText.isNullOrEmpty()){
          mAdapter.filterData(newText)
       } else {
           mainViewModel.readStudents.observe(viewLifecycleOwner){database ->
               mAdapter.setData(database.first().student)

           }

       }
        return true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}