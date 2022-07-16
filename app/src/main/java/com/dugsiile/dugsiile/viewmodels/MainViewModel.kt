package com.dugsiile.dugsiile.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.*
import com.dugsiile.dugsiile.data.DataStoreRepository
import com.dugsiile.dugsiile.data.Repository
import com.dugsiile.dugsiile.data.database.StudentsEntity
import com.dugsiile.dugsiile.data.database.UserEntity
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


    /** ROOM DATABASE */
    val readStudents: LiveData<List<StudentsEntity>> = repository.local.readStudents().asLiveData()
    val readUser: LiveData<List<UserEntity>> = repository.local.readUser().asLiveData()

    private fun insertStudents(studentsEntity: StudentsEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertStudents(studentsEntity)
        }

    private fun insertUser(userEntity: UserEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertUser(userEntity)
        }


    /** RETROFIT */
    var logedinUser: MutableLiveData<NetworkResult<User>> = MutableLiveData()
    var addStudentResponse: MutableLiveData<NetworkResult<StudentData>> = MutableLiveData()
    var studentsResponse: MutableLiveData<NetworkResult<Student>> = MutableLiveData()
    var deleteStudentsResponse: MutableLiveData<NetworkResult<DeleteResponse>> = MutableLiveData()
    var chargeAllResponse: MutableLiveData<NetworkResult<Fee>> = MutableLiveData()
    var chargeStudentResponse: MutableLiveData<NetworkResult<ChargeSingleStudent>> = MutableLiveData()
    var receivePaymentResponse: MutableLiveData<NetworkResult<ChargeSingleStudent>> = MutableLiveData()
    var getFeesResponse: MutableLiveData<NetworkResult<Fee>> = MutableLiveData()
    var deleteFeeResponse: MutableLiveData<NetworkResult<DeleteResponse>> = MutableLiveData()

    fun getLogedinUser(token: String) = viewModelScope.launch {
        getLogedinUserSafeCall(token)
    }
    fun getStudents(token: String) = viewModelScope.launch {
        getStudentsSafeCall(token)
    }

    fun addStudent(token: String,studentData: StudentData)= viewModelScope.launch{
        addStudentSafeCall(token,studentData)
    }
    fun deleteStudent(token: String,_id: String)= viewModelScope.launch{
        deleteStudentSafeCall(token,_id)
    }
    fun chargeAllPaidStudents(token: String)= viewModelScope.launch{
        chargeAllPaidStudentsSafeCall(token)
    }
    fun chargeStudent(token: String, id: String,amountCharged: AmountFee)= viewModelScope.launch{
        chargeStudentSafeCall(token,id,amountCharged)
    }
    fun receivePayment(token: String, id: String)= viewModelScope.launch{
        receivePaymentSafeCall(token,id)
    }
    fun getFees(token: String, queries: Map<String,String>)= viewModelScope.launch{
        getFeesSafeCall(token,queries)
    }
    fun deleteFee(token: String,_id: String)= viewModelScope.launch{
        deleteFeeSafeCall(token,_id)
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
                Log.d("Token", token)

            } catch (e: Exception) {
                logedinUser.value = NetworkResult.Error(e.message)
            }
        } else {
            logedinUser.value = NetworkResult.Error("No Internet Connection.")
        }


    }

    private suspend fun getStudentsSafeCall(token: String) {
        studentsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getStudents(token)
                studentsResponse.value = handleStudentsResponse(response)

            } catch (e: Exception) {
                studentsResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            studentsResponse.value = NetworkResult.Error("No Internet Connection.")
        }


    }

    private suspend fun deleteStudentSafeCall(token: String,_id: String) {
        deleteStudentsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.deleteStudent(token,_id)
                deleteStudentsResponse.value = handleDeleteStudentResponse(response)

            } catch (e: Exception) {
                deleteStudentsResponse.value = NetworkResult.Error(e.message)
                Log.d("Delete student error", e.toString())
            }
        } else {
            deleteStudentsResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun chargeAllPaidStudentsSafeCall(token: String) {
        chargeAllResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.chargeAllPaidStudents(token)
                chargeAllResponse.value = handleChargeAll(response)

                Log.d("add student Response", addStudentResponse.value?.data.toString())

            } catch (e: Exception) {
                addStudentResponse.value = NetworkResult.Error(e.message)
                Log.d("charge all error", e.toString())
            }
        } else {
            chargeAllResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun chargeStudentSafeCall(token: String, id: String,amountCharged:AmountFee) {
        chargeStudentResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.chargeStudent(token,id, amountCharged)
                chargeStudentResponse.value = handleChargeStudent(response)

            } catch (e: Exception) {
                chargeStudentResponse.value = NetworkResult.Error(e.message)
                Log.d("charge student error", e.toString())
            }
        } else {
            chargeStudentResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun receivePaymentSafeCall(token: String, id: String) {
        receivePaymentResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.receivePayment(token,id)
                receivePaymentResponse.value = handleReceivePayment(response)

            } catch (e: Exception) {
                receivePaymentResponse.value = NetworkResult.Error(e.message)
                Log.d("receive payment error", e.toString())
            }
        } else {
            receivePaymentResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun getFeesSafeCall(token: String, queries: Map<String, String>) {
        getFeesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getFees(token,queries)
                getFeesResponse.value = handleGetFeesResponse(response)

            } catch (e: Exception) {
                getFeesResponse.value = NetworkResult.Error(e.message)
                Log.d("get fees error", e.toString())
            }
        } else {
            getFeesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun deleteFeeSafeCall(token: String,_id: String) {
        deleteFeeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.deleteFee(token,_id)
                deleteFeeResponse.value = handleDeleteFeeResponse(response)

            } catch (e: Exception) {
                deleteFeeResponse.value = NetworkResult.Error(e.message)
                Log.d("Delete Fee error", e.toString())
            }
        } else {
            deleteFeeResponse.value = NetworkResult.Error("No Internet Connection.")
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
                val user = response.body()!!.data
                offlineCacheUser(user)
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleStudentsResponse(response: Response<Student>): NetworkResult<Student> {
        return when {
            response.code() == 401 -> {
                NetworkResult.Error("Not Authorized to access this Resource.")
            }
            response.isSuccessful -> {
                val student = response.body()
                if(student != null) {
                    offlineCacheStudents(student)
                }
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }
    private fun handleDeleteStudentResponse(response: Response<DeleteResponse>): NetworkResult<DeleteResponse> {
        return when {
            response.code() == 401 ->{
                NetworkResult.Error("Not Authorized to access this Resource.")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                Log.d("Delete Student error", response.message())
                NetworkResult.Error(response.message())
            }
        }
    }
    private fun handleChargeAll(response: Response<Fee>): NetworkResult<Fee> {
        return when {
            response.code() == 401 ->{
                NetworkResult.Error("Not Authorized to access this Resource.")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                Log.d("charge all error", response.message())
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleChargeStudent(response: Response<ChargeSingleStudent>): NetworkResult<ChargeSingleStudent> {
        return when {
            response.code() == 401 ->{
                NetworkResult.Error("Not Authorized to access this Resource.")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)

            }
            else -> {
                Log.d("charge student error", response.message())
                NetworkResult.Error(response.message())
            }
        }
    }
    private fun handleReceivePayment(response: Response<ChargeSingleStudent>): NetworkResult<ChargeSingleStudent> {
        return when {
            response.code() == 401 ->{
                NetworkResult.Error("Not Authorized to access this Resource.")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)

            }
            else -> {
                Log.d("receive payment error", response.message())
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleGetFeesResponse(response: Response<Fee>): NetworkResult<Fee> {
        return when {
            response.code() == 401 ->{
                NetworkResult.Error("Not Authorized to access this Resource.")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)

            }
            else -> {
                Log.d("get fees error", response.message())
                NetworkResult.Error(response.message())
            }
        }
    }
    private fun handleDeleteFeeResponse(response: Response<DeleteResponse>): NetworkResult<DeleteResponse> {
        return when {
            response.code() == 401 ->{
                NetworkResult.Error("Not Authorized to access this Resource.")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!)
            }
            else -> {
                Log.d("Delete Fee error", response.message())
                NetworkResult.Error(response.message())
            }
        }
    }

    // offline caching

    private fun offlineCacheStudents(student: Student) {
        val studentsEntity = StudentsEntity(student)
        insertStudents(studentsEntity)
    }

    private fun offlineCacheUser(user: UserData) {
        val userEntity = UserEntity(user)
        insertUser(userEntity)
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