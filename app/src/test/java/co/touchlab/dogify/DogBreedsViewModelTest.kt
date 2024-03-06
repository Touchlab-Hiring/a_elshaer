package co.touchlab.dogify

import app.cash.turbine.test
import co.touchlab.dogify.data.DogRepository
import co.touchlab.dogify.model.DogBreed
import co.touchlab.dogify.ui.DogBreedsViewModel
import co.touchlab.dogify.ui.UiState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DogBreedsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: DogBreedsViewModel
    private val mockDogsRepository = mockk<DogRepository>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { mockDogsRepository.refreshDogBreeds(any()) } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDogBreeds emits Success state with data`() = runTest {
        // Arrange
        val mockData = listOf(DogBreed("Breed1", ""), DogBreed("Breed2", ""))
        every { mockDogsRepository.dogBreeds } returns flowOf(mockData)
        // Act
        viewModel = DogBreedsViewModel(mockDogsRepository)
        // Assert
        viewModel.uiState.test {
            assertEquals(UiState.Idle, awaitItem())
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Idle, awaitItem())
        }
        viewModel.data.test {
            assertEquals(mockData, awaitItem())
        }
    }

    @Test
    fun `loadDogBreeds emits Error state on exception`() = runTest {
        // Arrange
        val exception = CancellationException("Error fetching dog breeds")
        every { mockDogsRepository.dogBreeds } returns flow { throw exception }

        // Act
        viewModel = DogBreedsViewModel(mockDogsRepository)

        // Assert
        viewModel.data.test {
            assertEquals(awaitItem(), emptyList<DogBreed>())
        }
        viewModel.uiState.test {
            assertEquals(UiState.Idle, awaitItem())
            assertEquals(UiState.Error(exception), awaitItem())
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Idle, awaitItem())
        }

    }

    @Test
    fun `refreshDogBreeds does not proceed when already in Loading state`() = runTest {
        // Arrange
        every { mockDogsRepository.dogBreeds } returns flowOf(emptyList())
        viewModel = DogBreedsViewModel(mockDogsRepository)

        viewModel.refreshDogBreeds() // Set state to Loading

        // Act
        val initialState = viewModel.uiState.value
        val initialData = viewModel.data.value
        viewModel.refreshDogBreeds() // Attempt to refresh again

        // Assert
        assertEquals(initialState, viewModel.uiState.value)
        assertEquals(initialData, viewModel.data.value)
    }

    @Test
    fun `refreshDogBreeds emits Success state after refresh`() = runTest {
        // Arrange
        val mockFlow = MutableStateFlow(emptyList<DogBreed>())
        every { mockDogsRepository.dogBreeds } returns mockFlow
        // Act
        viewModel = DogBreedsViewModel(mockDogsRepository)
        // Assert
        viewModel.uiState.test {
            assertEquals(UiState.Idle, awaitItem())
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Idle, awaitItem())
        }
        viewModel.data.test {
            assertEquals(emptyList<DogBreed>(), awaitItem())
        }
        // Arrange
        val mockDataAfterRefresh = listOf(DogBreed("Breed3", ""))
        coEvery { mockDogsRepository.refreshDogBreeds(true) } just Runs

        // Act
        viewModel.refreshDogBreeds()
        mockFlow.value = mockDataAfterRefresh
        // Assert
        viewModel.uiState.test {
            assertEquals(UiState.Idle, awaitItem())
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Idle, awaitItem())
        }
        viewModel.data.test {
            assertEquals(mockDataAfterRefresh, awaitItem())
        }
    }
}
