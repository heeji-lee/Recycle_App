package com.appliances.recycle.notice

data class Member(
    val email: String,
    val mname: String,
    val pw: String,
    val address: String,
    val phone: String,
    val social: Boolean,
    val del: Boolean,
    val roleSet: List<String>
)