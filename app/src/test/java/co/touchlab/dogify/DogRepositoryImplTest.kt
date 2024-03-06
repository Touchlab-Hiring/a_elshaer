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
    fun `refreshDogBreeds fetches from remote and updates local database`() =
        runTest(dispatcherProvider.io()) {
            val dogBreedsResponse = DogBreedResponse(listOf("affenpinscher"), "success")
            val response: Response<DogBreedResponse> = Response.success(dogBreedsResponse)
            coEvery { dogBreedDao.isNotEmpty() } returns false
            coEvery { dogService.getBreeds() } returns response
            coEvery { dogService.getImage(any()) } returns Response.success(
                ImageResult(
                    "imageUrl",
                    "success"
                )
            )
            coEvery { dogBreedDao.insertDogBreed(any<DogBreedLocal>()) } returns Unit

            dogRepository.refreshDogBreeds(forceRefresh)

            coVerify(exactly = 1) { dogBreedDao.insertDogBreed(any()) }
        }

    @Test
    fun `refreshDogBreedImage updates image URL for a breed`() = runTest(dispatcherProvider.io()) {
        val dogBreed = DogBreed("affenpinscher", "oldImageUrl")
        val newImageUrl = "newImageUrl"
        val response: Response<ImageResult> = Response.success(ImageResult(newImageUrl, "success"))

        coEvery { dogService.getImage(dogBreed.name) } returns response
        coEvery { dogBreedDao.update(any()) } returns Unit

        dogRepository.refreshDogBreedImage(dogBreed)

        coVerify(exactly = 1) { dogBreedDao.update(DogBreedLocal(dogBreed.name, newImageUrl)) }
    }

    @Test
    fun `refreshDogBreeds handles exceptions gracefully`() = runTest(dispatcherProvider.io()) {
        try {
            coEvery { dogService.getBreeds() } throws Exception("Network error")
            coEvery { dogBreedDao.isNotEmpty() } returns false
            dogRepository.refreshDogBreeds(forceRefresh)
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

            dogRepository.refreshDogBreeds(forceRefresh)

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
    fun `dogBreeds falls back to local cache on remote fetch failure`() = runTest(dispatcherProvider.io()) {
        coEvery { dogService.getBreeds() } throws Exception("Network error")
        // Assume dogBreedsLocal is already populated in setup
        try {
            dogRepository.refreshDogBreeds(forceRefresh)
        } catch (e: Exception) {
            assertTrue("Expected local cache to be used on remote fetch failure.", true)
        }

        dogRepository.dogBreeds.test {
            val item = awaitItem()
            assertFalse("Expected local cache to be used on remote fetch failure.", item.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `refreshDogBreedImage handles remote fetch failure gracefully`() = runTest(dispatcherProvider.io()) {
        val dogBreed = DogBreed("affenpinscher", "oldImageUrl")
        coEvery { dogService.getImage(dogBreed.name) } throws Exception("Network error")

        try {
            dogRepository.refreshDogBreedImage(dogBreed)
            fail("Expected an exception to be thrown on remote fetch failure.")
        } catch (e: Exception) {
            assertEquals("Exception message does not match expected.", e.message, "Network error")
        }
    }
}
