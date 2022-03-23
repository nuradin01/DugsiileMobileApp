package com.dugsiile.dugsiile.data

import com.dugsiile.dugsiile.data.network.DugsiileApi
import com.dugsiile.dugsiile.models.EmailAndPassword
import com.dugsiile.dugsiile.models.Token
import com.dugsiile.dugsiile.models.UploadImageResponse
import com.dugsiile.dugsiile.models.UserData
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

    suspend fun uploadImage(image: MultipartBody.Part): Response<UploadImageResponse> {
        return dugsiileApi.uploadImage(image)
    }
}