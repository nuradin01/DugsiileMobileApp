package com.dugsiile.dugsiile.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dugsiile.dugsiile.data.DataStoreRepository
import com.dugsiile.dugsiile.data.Repository
import com.dugsiile.dugsiile.models.*
import com.dugsiile.dugsiile.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    val readToken = dataStoreRepository.readToken

    fun signout() =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.signout()
        }

    /** RETROFIT */
    var logedinUser: MutableLiveData<NetworkResult<User>> = MutableLiveData()
    var addStudentResponse: MutableLiveData<NetworkResult<StudentData>> = MutableLiveData()

    fun getLogedinUser(token: String) = viewModelScope.launch {
        getLogedinUserSafeCall(token)
    }

    fun addStudent(token: String,studentData: StudentData)= viewModelScope.launch{
        addStudentSafeCall(token,studentData)
    }

    private suspend fun addStudentSafeCall(token: String,studentData: StudentData) {
        addStudentResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.addStudent(token,studentData)
                addStudentResponse.value = handleAddStudent(response)
                Log.d("add student data", studentData.toString())
                Log.d("add token", token)

                Log.d("add student Response", addStudentResponse.value?.data.toString())

            } catch (e: Exception) {
                addStudentResponse.value = NetworkResult.Error(e.message)
                Log.d("SignUp error", e.toString())
            }
        } else {
            addStudentResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }


    private suspend fun getLogedinUserSafeCall(token: String) {
        logedinUser.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getLogedinUser(token)
                logedinUser.value = handleLogedinUser(response)

            } catch (e: Exception) {
                logedinUser.value = NetworkResult.Error(e.message)
            }
        } else {
            logedinUser.value = NetworkResult.Error("No Internet Connection.")
        }


    }

    private fun handleAddStudent(response: Response<StudentData>): NetworkResult<StudentData> {
        return when {
            response.code() == 401 ->{
                NetworkResult.Error("Not Authorized to access this Resource.")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                Log.d("SignUp error", response.message())
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleLogedinUser(response: Response<User>): NetworkResult<User> {
        return when {
            response.code() == 401 -> {
                NetworkResult.Error("Not Authorized to access this Resource.")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}