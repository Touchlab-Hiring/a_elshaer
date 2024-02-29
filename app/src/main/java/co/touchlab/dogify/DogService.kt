package co.touchlab.dogify

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DogService {
    @GET("breeds/list")
    fun getBreeds(): Call<NameResult>

    @GET("breed/{name}/images/random")
    fun getImage(@Path("name") name: String): Call<ImageResult>
}
