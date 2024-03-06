package co.touchlab.dogify.data.remote.model

import co.touchlab.dogify.model.DogBreed
import co.touchlab.dogify.data.local.database.DogBreedLocal
import retrofit2.Response

fun DogBreedLocal.toDogBreed(): DogBreed {
    return DogBreed(name, imageUrl)
}

fun <T> Response<T>.unwrapResponse(): T {
    if (body() == null) throw Exception("Empty breed list")
    return when (code()) {
        in 200..299 -> body()!!
        401 -> throw Exception("Unauthorized")
        404 -> throw Exception("Dog breed not found")
        in 500..599 -> throw Exception("Server error")
        else -> throw Exception(errorBody()?.string() ?: "Unknown error")
    }
}