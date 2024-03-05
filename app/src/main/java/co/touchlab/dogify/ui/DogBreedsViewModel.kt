package co.touchlab.dogify.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.dogify.data.DogRepository
import co.touchlab.dogify.model.DogBreed
import co.touchlab.dogify.ui.UiState.Error
import co.touchlab.dogify.ui.UiState.Loading
import co.touchlab.dogify.ui.UiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogBreedsViewModel @Inject constructor(
    private val dogsRepository: DogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadDogBreeds()
    }

    private fun loadDogBreeds() {
        viewModelScope.launch {
            // Check if we are already in a Loading state before proceeding
            if (_uiState.value is Loading) return@launch
            _uiState.value = Loading
            try {
                dogsRepository.dogBreeds
                    .collect { breeds ->
                        _uiState.value = Success(breeds)
                    }
            } catch (e: Exception) {
                _uiState.value = Error(e)
            }
        }
    }

    fun refreshDogBreeds() {
        viewModelScope.launch {
            // Guard against initiating a refresh if we're already in a Loading state
            if (_uiState.value is Loading) return@launch

            _uiState.value = Loading
            try {
                dogsRepository.refreshDogBreeds()
            } catch (e: Exception) {
                _uiState.value = Error(e)
            }
        }
    }
}

sealed class UiState{
    data object Idle : UiState()
    data object Loading: UiState()
    data class Success(val data: List<DogBreed>): UiState()
    data class Error(val message: Throwable): UiState()
}