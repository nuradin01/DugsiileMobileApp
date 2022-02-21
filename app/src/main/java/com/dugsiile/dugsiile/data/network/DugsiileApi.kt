package com.dugsiile.dugsiile.data.network

import com.dugsiile.dugsiile.models.EmailAndPassword
import com.dugsiile.dugsiile.models.Token
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DugsiileApi {


    @POST("api/v1/auth/login")
    suspend fun login(
//        @Field ("email") email: String,
//        @Field ("password") password: String
        @Body emailAndPassword: EmailAndPassword
    ) : Response<Token>
}