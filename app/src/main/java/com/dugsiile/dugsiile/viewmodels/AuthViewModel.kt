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
import com.dugsiile.dugsiile.models.EmailAndPassword
import com.dugsiile.dugsiile.models.Token
import com.dugsiile.dugsiile.models.UploadImageResponse
import com.dugsiile.dugsiile.models.UserData
import com.dugsiile.dugsiile.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {


    fun saveToken(token: String) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveToken(token)
        }

    /** RETROFIT */
    var loginResponse: MutableLiveData<NetworkResult<Token>> = MutableLiveData()
    var signUpResponse: MutableLiveData<NetworkResult<Token>> = MutableLiveData()
    var uploadImageResponse: MutableLiveData<NetworkResult<UploadImageResponse>> = MutableLiveData()

    fun login(emailAndPassword: EmailAndPassword) = viewModelScope.launch {
        loginSafeCall(emailAndPassword)
    }
    fun uploadImage(image: MultipartBody.Part) = viewModelScope.launch {
        uploadImageSafeCall(image)
    }
    fun signUp(userData: UserData)= viewModelScope.launch{
        signUpSafeCall(userData)
    }

    private suspend fun signUpSafeCall(userData: UserData) {
        signUpResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.signUp(userData)
                signUpResponse.value = handleSignUpResponse(response)
                Log.d("sign Up data", userData.toString())

                Log.d("signUp Response", signUpResponse.value?.data?.token.toString())

            } catch (e: Exception) {
                signUpResponse.value = NetworkResult.Error("Something went wrong.")
                Log.d("SignUp error", e.toString())
            }
        } else {
            signUpResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun loginSafeCall(emailAndPassword: EmailAndPassword) {
        loginResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.login(emailAndPassword)
                loginResponse.value = handleLoginResponse(response)

                Log.d("guul", loginResponse.value?.message.toString())

            } catch (e: Exception) {
                loginResponse.value = NetworkResult.Error("Something went wrong.")
                Log.d("error", e.toString())
            }
        } else {
            loginResponse.value = NetworkResult.Error("No Internet Connection.")
        }

    }
    private fun handleLoginResponse(response: Response<Token>): NetworkResult<Token> {
        return when {
            response.code() == 401 ->{
                    NetworkResult.Error("Invalid credentials")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleSignUpResponse(response: Response<Token>): NetworkResult<Token> {
        return when {
            response.code()== 405 ->{
                NetworkResult.Error("The Email you entered is taken by an other account")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private suspend fun uploadImageSafeCall(image: MultipartBody.Part) {
        uploadImageResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.uploadImage(image)
                uploadImageResponse.value = handleUploadImageResponse(response)
                Log.d("uploadImage", uploadImageResponse.value?.data.toString())

            } catch (e: Exception) {
                uploadImageResponse.value = NetworkResult.Error("Something went wrong.")
                Log.d("uploadImage", e.toString())
            }
        } else {
            uploadImageResponse.value = NetworkResult.Error("No Internet Connection.")
        }

    }

    private fun handleUploadImageResponse(response: Response<UploadImageResponse>): NetworkResult<UploadImageResponse> {
        return when {
            response.code() == 500 ->{
                NetworkResult.Error("Something went wrong")
            }
            response.isSuccessful -> {
                Log.d("uploadImage", response.body()!!.image.toString())
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