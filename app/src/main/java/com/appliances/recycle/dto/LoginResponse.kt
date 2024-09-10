package com.appliances.recycle.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
)