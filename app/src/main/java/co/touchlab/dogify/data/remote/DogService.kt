package co.touchlab.dogify.data.remote

import co.touchlab.dogify.data.remote.model.DogBreedResponse
import co.touchlab.dogify.data.remote.model.ImageResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DogService {
    @GET("breeds/list")
    suspend fun getBreeds(): Response<DogBreedResponse>

    @GET("breed/{name}/images/random")
    suspend fun getImage(@Path("name") name: String): Response<ImageResult>
}
