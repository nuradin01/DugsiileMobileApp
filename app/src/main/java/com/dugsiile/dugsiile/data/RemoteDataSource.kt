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
}