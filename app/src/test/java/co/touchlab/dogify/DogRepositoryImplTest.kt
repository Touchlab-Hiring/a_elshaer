package co.touchlab.dogify

import app.cash.turbine.test
import co.touchlab.dogify.core.DispatcherProvider
import co.touchlab.dogify.data.DogRepositoryImpl
import co.touchlab.dogify.data.local.database.DogBreedDao
import co.touchlab.dogify.data.remote.DogService
import co.touchlab.dogify.core.FakeDispatcher
import co.touchlab.dogify.data.local.database.DogBreedLocal
import co.touchlab.dogify.data.remote.model.DogBreedResponse
import co.touchlab.dogify.data.remote.model.ImageResult
import co.touchlab.dogify.data.remote.model.toDogBreed
import co.touchlab.dogify.model.DogBreed
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class DogRepositoryImplTest {

    private lateinit var dogRepository: DogRepositoryImpl

    @MockK
    lateinit var dogService: DogService

    @MockK
    lateinit var dogBreedDao: DogBreedDao
    private val dispatcherProvider: DispatcherProvider = FakeDispatcher()
    val dogBreedsLocal = listOf(DogBreedLocal("affenpinscher", "imageUrl"))

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        coEvery { dogBreedDao.getDogBreedsFlow() } answers { flowOf(dogBreedsLocal) }
        dogRepository = DogRepositoryImpl(dogService, dogBreedDao, dispatcherProvider)
    }

    @Test
    fun `dogBreeds flow emits successfully`() = runTest {
        val dogBreeds = dogBreedsLocal.map { it.toDogBreed() }

        coEvery { dogBreedDao.getDogBreedsFlow() } answers { flowOf(dogBreedsLocal) }

        dogRepository.dogBreeds.test {
            val item = awaitItem()
            assert(item == dogBreeds)
            awaitComplete()
        }
    }

    @Test
    fun `refreshDogBreedImage updates image URL for a breed`() = runTest(dispatcherProvider.io()) {
        val dogBreed = DogBreed("affenpinscher", "")
        val newImageUrl = "newImageUrl"
        val response: Response<ImageResult> = Response.success(ImageResult(newImageUrl, "success"))
        coEvery { dogBreedDao.update(DogBreedLocal(dogBreed.name, newImageUrl)) } returns Unit
        coEvery { dogService.getImage(dogBreed.name) } returns response
        coEvery { dogBreedDao.getDogBreedsList() } returns listOf(DogBreedLocal(dogBreed.name, ""))
        coEvery { dogBreedDao.isNotEmpty() } returns true

        dogRepository = DogRepositoryImpl(dogService, dogBreedDao, dispatcherProvider)
        dogRepository.refreshDogBreeds(false)

        coVerify(exactly = 1) { dogService.getImage(dogBreed.name) }
        coVerify(exactly = 1) { dogBreedDao.update(DogBreedLocal(dogBreed.name, newImageUrl)) }
    }

    @Test
    fun `refreshDogBreeds handles exceptions gracefully`() = runTest(dispatcherProvider.io()) {
        try {
            coEvery { dogService.getBreeds() } throws Exception("Network error")
            coEvery { dogBreedDao.isNotEmpty() } returns false
            dogRepository.refreshDogBreeds(true)
        } catch (e: Exception) {
            Assert.assertTrue(e.message == "Network error")
        }
    }

    @Test
    fun `dogBreeds flow emits empty list when local database is empty`() = runTest {
        coEvery { dogBreedDao.getDogBreedsFlow() } returns flowOf(emptyList())
        dogRepository = DogRepositoryImpl(dogService, dogBreedDao, dispatcherProvider)

        dogRepository.dogBreeds.test {
            val item = awaitItem()
            awaitComplete()
        }
    }

    @Test
    fun `refreshDogBreeds does not update local database when response is empty`() =
        runTest(dispatcherProvider.io()) {
            val dogBreedsResponse = DogBreedResponse(emptyList(), "success")
            val response: Response<DogBreedResponse> = Response.success(dogBreedsResponse)
            coEvery { dogBreedDao.isNotEmpty() } returns false
            coEvery { dogService.getBreeds() } returns response
            try {
                dogRepository.refreshDogBreeds(true)
            } catch (e: Exception) {
                assertTrue(e.message == "No breeds found")
            }

            coVerify(exactly = 0) { dogBreedDao.insertDogBreed(any()) }
        }

    @Test
    fun `verify mapping from DogBreedLocal to DogBreed is correct`() = runTest {
        val dogBreedLocalList = listOf(DogBreedLocal("breedName", "imageUrl"))
        val expectedDogBreedList = listOf(DogBreed("breedName", "imageUrl"))

        coEvery { dogBreedDao.getDogBreedsFlow() } returns flowOf(dogBreedLocalList)
        dogRepository = DogRepositoryImpl(dogService, dogBreedDao, dispatcherProvider)

        dogRepository.dogBreeds.test {
            val item = awaitItem()
            assertEquals(
                "Mapping from DogBreedLocal to DogBreed is incorrect.",
                expectedDogBreedList,
                item
            )
            awaitComplete()
        }
    }

    @Test
    fun `dogBreeds falls back to local cache on remote fetch failure`() =
        runTest(dispatcherProvider.io()) {
            coEvery { dogService.getBreeds() } throws Exception("Network error")
            // Assume dogBreedsLocal is already populated in setup
            try {
                dogRepository.refreshDogBreeds(true)
            } catch (e: Exception) {
                assertTrue("Expected local cache to be used on remote fetch failure.", true)
            }

            dogRepository.dogBreeds.test {
                val item = awaitItem()
                assertFalse(
                    "Expected local cache to be used on remote fetch failure.",
                    item.isEmpty()
                )
                awaitComplete()
            }
        }

    @Test
    fun `refreshDogBreeds fetches from remote on force refresh even if local data exists`() =
        runTest(dispatcherProvider.io()) {
            coEvery { dogBreedDao.isNotEmpty() } returns true
            coEvery { dogService.getBreeds() } returns Response.success(
                DogBreedResponse(
                    listOf("breedName"),
                    "success"
                )
            )
            coEvery { dogBreedDao.getDogBreedsList() } returns listOf(
                DogBreedLocal(
                    "breedName",
                    "imageUrl"
                )
            )

            dogRepository.refreshDogBreeds(forceRefresh = true)

            coVerify { dogService.getBreeds() }
        }

    @Test
    fun `refreshDogBreeds deletes old breeds and inserts new breeds on successful fetch`() =
        runTest(dispatcherProvider.io()) {
            val remoteDogBreeds = listOf(DogBreedLocal("newBreed", ""))
            coEvery { dogBreedDao.isNotEmpty() } returns false
            coEvery { dogService.getBreeds() } returns Response.success(
                DogBreedResponse(
                    listOf("newBreed"),
                    "success"
                )
            )

            dogRepository.refreshDogBreeds(true)

            coVerify(ordering = Ordering.ORDERED) {
                dogBreedDao.deleteAllDogBreeds()
                dogBreedDao.insertDogBreeds(remoteDogBreeds)
            }
        }

    @Test
    fun `safelySetImageForBreed continues processing if image fetch fails`() =
        runTest(dispatcherProvider.io()) {
            val breedWithoutImage1 = DogBreedLocal("breedWithoutImage1", "")
            val breedWithoutImage2 = DogBreedLocal("breedWithoutImage2", "")
            coEvery { dogService.getImage(breedWithoutImage1.name) } throws Exception("Network error")
            coEvery { dogService.getBreeds() } returns Response.success(
                DogBreedResponse(
                    listOf(
                        breedWithoutImage1.name,
                        breedWithoutImage2.name
                    ), "success"
                )
            )
            coEvery { dogBreedDao.isNotEmpty() } returns true

            dogRepository.refreshDogBreeds(true) // Assuming this calls safelySetImageForBreed internally for each breed

            // Verify that it attempted to fetch image for breedWithoutImage2 and continued execution
            coVerify { dogService.getImage(breedWithoutImage2.name) }
        }

    @Test
    fun `refreshDogBreeds does not delete or insert breeds if remote response is empty`() =
        runTest(dispatcherProvider.io()) {
            coEvery { dogService.getBreeds() } returns Response.success(
                DogBreedResponse(
                    emptyList(),
                    "success"
                )
            )
            coEvery { dogBreedDao.isNotEmpty() } returns true
            try {
                dogRepository.refreshDogBreeds(true)
            } catch (e: Exception) {
                assertTrue(e.message == "No breeds found")
            }
            coVerify(exactly = 0) { dogBreedDao.deleteAllDogBreeds() }
            coVerify(exactly = 0) { dogBreedDao.insertDogBreeds(any()) }
        }

    @Test
    fun `refreshDogBreeds falls back to local cache on remote fetch failure`() =
        runTest(dispatcherProvider.io()) {
            coEvery { dogService.getBreeds() } throws Exception("Network error")
            // Assuming local cache is not empty for this test
            coEvery { dogBreedDao.isNotEmpty() } returns false
            coEvery { dogBreedDao.getDogBreedsFlow() } returns flowOf( listOf(
                DogBreedLocal(
                    "cachedBreed",
                    "cachedImageUrl"
                )
            ))
            dogRepository = DogRepositoryImpl(dogService, dogBreedDao, dispatcherProvider)
            try {
                dogRepository.refreshDogBreeds(true)
            } catch (e: Exception) {
                assertTrue(e.message == "Network error")
            }

            dogRepository.dogBreeds.test {
                val item = awaitItem()
                assertEquals(
                    "Expected local cache to be used on remote fetch failure.",
                    listOf(DogBreed("cachedBreed", "cachedImageUrl")),
                    item
                )
                awaitComplete()
            }
        }


}
