package com.tecfort.navigationtest.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecfort.navigationtest.data.repository.HealthConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: HealthConnectRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState(requiredPermissions = repository.permissions))
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    /** Re-checks availability/permissions and reloads today's metrics; call after a permission request. */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            if (!repository.isAvailable()) {
                _uiState.update { it.copy(isLoading = false, isHealthConnectAvailable = false) }
                return@launch
            }

            if (!repository.hasAllPermissions()) {
                _uiState.update {
                    it.copy(isLoading = false, isHealthConnectAvailable = true, hasPermissions = false)
                }
                return@launch
            }

            val metrics = repository.getTodayMetrics()
            _uiState.update {
                it.copy(isLoading = false, isHealthConnectAvailable = true, hasPermissions = true, metrics = metrics)
            }
        }
    }
}
