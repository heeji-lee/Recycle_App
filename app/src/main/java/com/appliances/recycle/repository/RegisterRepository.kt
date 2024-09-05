package com.appliances.recycle.repository

import android.util.Log
import com.appliances.recycle.SerializedName.RegisterRequest
import com.appliances.recycle.network.ImageUploadApi
import okhttp3.ResponseBody
import retrofit2.Call

class RegisterRepository (private val apiService: ImageUploadApi) {

    fun RegisterUser(email: String, mname: String, pw: String, address:String, phone:String ): Call<ResponseBody> {
        val registerRequest = RegisterRequest(email,mname,pw, address, phone)
        Log.e("lsy", "레퍼지토리확인" + registerRequest)
        return apiService.join(registerRequest)
    }
}