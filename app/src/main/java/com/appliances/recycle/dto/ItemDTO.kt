package com.appliances.recycle.dto

data class ItemDTO(
    val ino: Long,
    val iname: String,
    val iprice: Long,
    val imageUrl: String?     // 이미지 URL (nullable)
)