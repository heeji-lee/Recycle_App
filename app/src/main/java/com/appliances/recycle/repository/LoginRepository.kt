package com.appliances.recycle.repository

import com.appliances.recycle.network.ImageUploadApi

class LoginRepository(private val apiService: ImageUploadApi) {

    suspend fun login(username: String, password: String): Boolean {
        val response = apiService.login(username, password)
        return response.isSuccessful // 로그인 성공 여부 반환
    }

}