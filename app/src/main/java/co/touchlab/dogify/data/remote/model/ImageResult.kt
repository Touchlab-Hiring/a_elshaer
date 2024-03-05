package co.touchlab.dogify.data.remote.model

import com.google.gson.annotations.SerializedName

data class ImageResult(
    @SerializedName("message")
    val url: String,
    val status: String,
)
