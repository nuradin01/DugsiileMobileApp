package com.dugsiile.dugsiile.data

import com.dugsiile.dugsiile.data.network.DugsiileApi
import com.dugsiile.dugsiile.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class RemoteDataSource  @Inject constructor(
    private val dugsiileApi: DugsiileApi
){
    suspend fun login(emailAndPassword: EmailAndPassword): Response<Token> {
        return dugsiileApi.login(emailAndPassword)
    }
    suspend fun signUp(userData: UserData): Response<Token> {
        return dugsiileApi.signUp(userData)
    }

    suspend fun getLogedinUser(token: String): Response<User> {
        return dugsiileApi.getLogedinUser(token)
    }

    suspend fun uploadImage(image: MultipartBody.Part): Response<UploadImageResponse> {
        return dugsiileApi.uploadImage(image)
    }

    suspend fun addStudent(token: String,studentData: StudentData): Response<StudentData> {
        return dugsiileApi.addStudent(token,studentData)
    }
    suspend fun getStudents(token: String): Response<Student> {
        return dugsiileApi.getStudents(token)
    }

    suspend fun deleteStudent(token: String, _id: String): Response<DeleteResponse> {
        return dugsiileApi.deleteStudent(token, _id)
    }

    suspend fun chargeAllPaidStudents(token: String): Response<Fee> {
        return dugsiileApi.chargeAllPaidStudents(token)
    }

    suspend fun chargeStudent(token: String, id: String,amountCharged:AmountFee): Response<ChargeSingleStudent> {
        return dugsiileApi.chargeStudent(token,id,amountCharged)
    }
    suspend fun receivePayment(token: String, id: String): Response<ChargeSingleStudent> {
        return dugsiileApi.receivePayment(token,id)
    }
    suspend fun getFees(token: String, queries: Map<String,String>): Response<Fee> {
        return dugsiileApi.getFees(token,queries)
    }

    suspend fun deleteFee(token: String, _id: String): Response<DeleteResponse> {
        return dugsiileApi.deleteFee(token, _id)
    }
}