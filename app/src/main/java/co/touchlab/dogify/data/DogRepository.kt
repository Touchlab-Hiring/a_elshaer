package co.touchlab.dogify.data

import co.touchlab.dogify.model.DogBreed
import co.touchlab.dogify.core.DispatcherProvider
import co.touchlab.dogify.data.local.database.DogBreedDao
import co.touchlab.dogify.data.local.database.DogBreedLocal
import co.touchlab.dogify.data.remote.DogService
import co.touchlab.dogify.data.remote.model.toDogBreed
import co.touchlab.dogify.data.remote.model.unwrapResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface DogRepository {
    val dogBreeds: Flow<List<DogBreed>>
    suspend fun refreshDogBreeds()
    suspend fun refreshDogBreedImage(dogBreed: DogBreed)
}

class DogRepositoryImpl @Inject constructor(
    private val dogService: DogService,
    private val dogBreedDao: DogBreedDao,
    private val dispatchers: DispatcherProvider
) : DogRepository {
    override val dogBreeds: Flow<List<DogBreed>> = dogBreedDao.getDogBreeds()
        .map { locals -> locals.map { it.toDogBreed() } }

    override suspend fun refreshDogBreeds() {
        if (dogBreedDao.isNotEmpty()) return
        withContext(dispatchers.io()) {
            val dogBreedsResponse = dogService.getBreeds().unwrapResponse()
            dogBreedsResponse.dogBreeds.map { breed ->
                try {
                    val imageUrl = dogService.getImage(breed).unwrapResponse().url
                    dogBreedDao.insertDogBreed(DogBreedLocal(breed, imageUrl))
                } catch (e: Exception) {
                    dogBreedDao.insertDogBreed(DogBreedLocal(breed, ""))
                }
            }
        }
    }

    override suspend fun refreshDogBreedImage(dogBreed: DogBreed) {
        withContext(dispatchers.io()) {
            val imageUrl = dogService.getImage(dogBreed.name).unwrapResponse().url
            dogBreedDao.update(DogBreedLocal(dogBreed.name, imageUrl))
        }
    }
}