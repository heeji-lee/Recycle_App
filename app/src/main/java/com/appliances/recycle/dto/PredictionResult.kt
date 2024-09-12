package com.appliances.recycle.dto

import com.google.gson.annotations.SerializedName

data class PredictionResult(
    @SerializedName("confidence") val confidence: Double,
    @SerializedName("predicted_class_index") val predictedClassIndex: Int,
    @SerializedName("predicted_class_label") val predictedClassLabel: String,
//    @SerializedName("class_confidences") val classConfidences: Map<String, Double>
    val imageUrl: String // 이미지 URL 추가
)