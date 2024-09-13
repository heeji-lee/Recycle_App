package com.appliances.recycle.network

import com.appliances.recycle.SerializedName.ImageClassificationResponse
import com.appliances.recycle.SerializedName.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface INetworkService {
    @Multipart
    @POST("/classify")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ImageClassificationResponse>

    @FormUrlEncoded
    @POST("/echopickup/member/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<Void>

    @POST("/echopickup/member/join")
    fun join(@Body registerRequest: RegisterRequest): Call<ResponseBody>

}