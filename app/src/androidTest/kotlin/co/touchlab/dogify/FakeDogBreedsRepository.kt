package co.touchlab.dogify

import co.touchlab.dogify.data.DogRepository
import co.touchlab.dogify.model.DogBreed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class FakeDogBreedsRepository : DogRepository {

    private val dogBreedImpl = MutableStateFlow<List<DogBreed>>(emptyList())
    override val dogBreeds: Flow<List<DogBreed>>
        get() = dogBreedImpl

    override suspend fun refreshDogBreeds(forceRefresh: Boolean) {
        // Simulate an error condition by throwing an exception
        dogBreedImpl.value = (1..100).map { randomDogBreed(it) }
        throw Exception("Simulated error")
    }

    private fun randomDogBreed(number: Int): DogBreed {
        return DogBreed("dog $number", "https://loremflickr.com/640/480")
    }
}