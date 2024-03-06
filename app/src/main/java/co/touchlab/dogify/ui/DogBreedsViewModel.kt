package co.touchlab.dogify.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.dogify.data.DogRepository
import co.touchlab.dogify.ui.UiState.Error
import co.touchlab.dogify.ui.UiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogBreedsViewModel @Inject constructor(
    private val dogsRepository: DogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    val data = dogsRepository.dogBreeds
        .catch { e -> _uiState.value = Error(e) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        refreshDogBreeds()
    }

    fun refreshDogBreeds(forceRefresh : Boolean = false) {
        viewModelScope.launch {
            if (_uiState.value is Loading) return@launch
            _uiState.value = Loading
            try {
                dogsRepository.refreshDogBreeds(forceRefresh)
                _uiState.value = UiState.Idle
            } catch (e: Exception) {
                _uiState.value = Error(e)
            }
        }
    }
}

sealed class UiState{
    data object Idle : UiState()
    data object Loading: UiState()
    data class Error(val exception: Throwable): UiState()
}