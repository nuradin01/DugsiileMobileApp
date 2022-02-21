package com.dugsiile.dugsiile.data

import com.dugsiile.dugsiile.data.network.DugsiileApi
import com.dugsiile.dugsiile.models.EmailAndPassword
import com.dugsiile.dugsiile.models.Token
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource  @Inject constructor(
    private val dugsiileApi: DugsiileApi
){
    suspend fun login(emailAndPassword: EmailAndPassword): Response<Token> {
        return dugsiileApi.login(emailAndPassword)
    }
}