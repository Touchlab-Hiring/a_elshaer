package co.touchlab.dogify.data.remote.model

import com.google.gson.annotations.SerializedName

data class DogBreedResponse(
    @SerializedName("message")
    val dogBreeds: List<String>,

    @SerializedName("status")
    val status: String
)