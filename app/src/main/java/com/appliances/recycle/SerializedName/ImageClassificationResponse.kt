package com.appliances.recycle.SerializedName

import com.google.gson.annotations.SerializedName

data class ImageClassificationResponse(
    @SerializedName("predicted_class_label")
    val result: String?,

    @SerializedName("confidence")
    val confidence: Double
)