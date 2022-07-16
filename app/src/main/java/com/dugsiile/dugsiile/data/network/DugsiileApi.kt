package com.dugsiile.dugsiile.data.network

import com.dugsiile.dugsiile.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


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

    @GET("api/v1/auth/me")
    suspend fun getLogedinUser(
        @Header("authorization") token: String
    ) : Response<User>

    @POST("api/v1/students")
    suspend fun addStudent(
        @Header("authorization") token: String,
        @Body studentData: StudentData
    ) : Response<StudentData>

    @GET("api/v1/students")
    suspend fun getStudents(
        @Header("authorization") token: String,
    ) : Response<Student>

    @DELETE("api/v1/students/{id}")
    suspend fun deleteStudent(
        @Header("authorization") token: String,
        @Path ("id") id: String
    ) : Response<DeleteResponse>

    @POST("api/v1/fees")
    suspend fun chargeAllPaidStudents(
        @Header("authorization") token: String,
    ) : Response<Fee>
    @POST("api/v1/fees/{id}")
    suspend fun chargeStudent(
        @Header("authorization") token: String,
        @Path ("id") id: String,
        @Body amountCharged: AmountFee
    ) : Response<ChargeSingleStudent>
    @PUT("api/v1/fees/{id}")
    suspend fun receivePayment(
        @Header("authorization") token: String,
        @Path ("id") id: String
    ) : Response<ChargeSingleStudent>

    @GET("api/v1/fees")
    suspend fun getFees(
        @Header("authorization") token: String,
        @QueryMap queries: Map<String,String>
    ) : Response<Fee>

    @DELETE("api/v1/fees/{id}")
    suspend fun deleteFee(
        @Header("authorization") token: String,
        @Path ("id") id: String
    ) : Response<DeleteResponse>

}