package co.touchlab.dogify.data

import android.util.Log
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
    suspend fun refreshDogBreeds(forceRefresh: Boolean = false)
    suspend fun refreshDogBreedImage(dogBreed: DogBreed)
}

class DogRepositoryImpl @Inject constructor(
    private val dogService: DogService,
    private val dogBreedDao: DogBreedDao,
    private val dispatchers: DispatcherProvider
) : DogRepository {

    override val dogBreeds: Flow<List<DogBreed>> = dogBreedDao.getDogBreedsFlow()
        .map { locals -> locals.map { it.toDogBreed() } }

    override suspend fun refreshDogBreeds(forceRefresh: Boolean) = withContext(dispatchers.io()) {
        val breeds = getLocalOrRemoteBreeds(forceRefresh)
        breeds.forEach { breed ->
            safelySetImageForBreed(breed)
        }
    }

    private suspend fun getLocalOrRemoteBreeds(forceRefresh: Boolean) = if (dogBreedDao.isNotEmpty() && !forceRefresh) {
        dogBreedDao.getDogBreedsList()
    } else {
        fetchFromRemoteAndStore()
    }

    private suspend fun fetchFromRemoteAndStore(): List<DogBreedLocal> {
        return dogService.getBreeds().unwrapResponse().dogBreeds.map { DogBreedLocal(it, "") }
            .also {
                dogBreedDao.deleteAllDogBreeds()
                dogBreedDao.insertDogBreeds(it)
            }
    }

    private suspend fun safelySetImageForBreed(breed: DogBreedLocal) {
        runCatching {
            setImageForBreed(breed.toDogBreed())
        }.onFailure { e ->
            Log.d("DogRepositoryImpl", "Error setting image for breed: ${breed.name}", e)
        }
    }

    private suspend fun setImageForBreed(dogBreed: DogBreed) {
        if (dogBreed.imageUrl.isNotBlank()) return
        val imageUrl = dogService.getImage(dogBreed.name).unwrapResponse().url
        dogBreedDao.update(DogBreedLocal(dogBreed.name, imageUrl))
    }

    override suspend fun refreshDogBreedImage(dogBreed: DogBreed) = withContext(dispatchers.io()) {
        setImageForBreed(dogBreed)
    }
}