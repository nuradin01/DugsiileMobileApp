package com.dugsiile.dugsiile.data.network

import com.dugsiile.dugsiile.models.EmailAndPassword
import com.dugsiile.dugsiile.models.Token
import com.dugsiile.dugsiile.models.UploadImageResponse
import com.dugsiile.dugsiile.models.UserData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface DugsiileApi {


    @POST("api/v1/auth/login")
    suspend fun login(
        @Body emailAndPassword: EmailAndPassword
    ) : Response<Token>

    @POST("api/v1/auth/register")
    suspend fun signUp(
        @Body userData: UserData
    ) : Response<Token>

    @Multipart
    @POST("api/v1/upload")
   suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>
}