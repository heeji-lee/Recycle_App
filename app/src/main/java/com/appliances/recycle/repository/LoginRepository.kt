package com.appliances.recycle.repository

import android.content.SharedPreferences
import com.appliances.recycle.dto.LoginRequest
import com.appliances.recycle.retrofit.INetworkService

class LoginRepository(private val apiService: INetworkService, private val sharedPreferences: SharedPreferences) {

    suspend fun login(username: String, password: String): Boolean {
        val loginRequest = LoginRequest(username, password)
        val response = apiService.login(loginRequest)

        return if (response.isSuccessful && response.body() != null) {
            val accessToken = response.body()?.accessToken
            val refreshToken = response.body()?.refreshToken
            val username = response.body()?.username

            // JWT 토큰을 SharedPreferences에 저장
            sharedPreferences.edit().putString("jwt_token", accessToken).apply()
            sharedPreferences.edit().putString("refreshToken", refreshToken).apply()
            sharedPreferences.edit().putString("username", username).apply()

            true
        } else {
            false
        }
    }

}


