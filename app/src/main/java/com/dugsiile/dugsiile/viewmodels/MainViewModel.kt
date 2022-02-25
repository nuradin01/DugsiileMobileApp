package com.dugsiile.dugsiile.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dugsiile.dugsiile.data.Repository
import com.dugsiile.dugsiile.models.EmailAndPassword
import com.dugsiile.dugsiile.models.Token
import com.dugsiile.dugsiile.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {
    /** RETROFIT */
    var loginResponse: MutableLiveData<NetworkResult<Token>> = MutableLiveData()

    fun login(emailAndPassword: EmailAndPassword) = viewModelScope.launch {
        loginSafeCall(emailAndPassword)
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