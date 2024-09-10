package com.appliances.recycle.dto

data class RegisterRequest(
    val email: String,
    val mname: String,
    val pw: String,
    val address: String,
    val phone: String
)
