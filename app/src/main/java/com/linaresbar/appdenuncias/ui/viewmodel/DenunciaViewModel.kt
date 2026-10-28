package com.linaresbar.appdenuncias.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linaresbar.appdenuncias.data.api.RetrofitClient
import com.linaresbar.appdenuncias.data.model.Denuncia
import com.linaresbar.appdenuncias.data.repository.DenunciaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FormState(
    val dni: String = "",
    val tipo: String = "ROBO",
    val direccion: String = "",
    val descripcion: String = "",
    val fecha: String = getCurrentFormattedDate(),
    val isSubmitting: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

data class UiState(
    val denuncias: List<Denuncia> = emptyList(),
    val pageIndex: Int = 0,
    val limit: Int = 5,
    val isLoadingList: Boolean = false,
    val hasMoreData: Boolean = true,
    val listError: String? = null,

    val searchDni: String = "",
    val searchResults: List<Denuncia> = emptyList(),
    val isSearching: Boolean = false,
    val searchError: String? = null,

    val selectedDenuncia: Denuncia? = null,
    val isAttending: Boolean = false,
    val actionMessage: String? = null,

    val currentBaseUrl: String = RetrofitClient.DEFAULT_BASE_URL
)

fun getCurrentFormattedDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}

class DenunciaViewModel(
    private val repository: DenunciaRepository = DenunciaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    init {
        cargarDenuncias(reset = true)
    }

    // --- BASE URL SETUP ---
    fun updateBaseUrl(newUrl: String) {
        RetrofitClient.updateBaseUrl(newUrl)
        _uiState.update { it.copy(currentBaseUrl = RetrofitClient.getBaseUrl()) }
        cargarDenuncias(reset = true)
    }

    // --- LIST AND PAGINATION ---
    fun cargarDenuncias(reset: Boolean = false) {
        viewModelScope.launch {
            val current = _uiState.value
            if (current.isLoadingList) return@launch

            val targetPage = if (reset) 0 else current.pageIndex
            _uiState.update {
                it.copy(
                    isLoadingList = true,
                    listError = null,
                    pageIndex = targetPage,
                    denuncias = if (reset) emptyList() else it.denuncias
                )
            }

            val result = repository.obtenerDenunciasPaginadas(page = targetPage, limit = current.limit)
            result.onSuccess { pageResponse ->
                val nuevasDenuncias = pageResponse.content
                _uiState.update { state ->
                    val listaActualizada = if (reset) nuevasDenuncias else state.denuncias + nuevasDenuncias
                    state.copy(
                        denuncias = listaActualizada,
                        isLoadingList = false,
                        pageIndex = targetPage + 1,
                        hasMoreData = !pageResponse.empty && (nuevasDenuncias.size >= current.limit)
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoadingList = false,
                        listError = error.localizedMessage ?: "Error desconocido"
                    )
                }
            }
        }
    }

    // --- SEARCH BY DNI ---
    fun onSearchDniChanged(dni: String) {
        _uiState.update { it.copy(searchDni = dni, searchError = null) }
    }

    fun buscarDenunciasPorDni() {
        val dni = _uiState.value.searchDni.trim()
        if (dni.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), searchError = "Ingrese un DNI válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, searchError = null) }
            val result = repository.obtenerDenunciasPorCiudadano(dni)
            result.onSuccess { lista ->
                _uiState.update {
                    it.copy(
                        searchResults = lista,
                        isSearching = false,
                        searchError = if (lista.isEmpty()) "No se encontraron denuncias para el DNI: $dni" else null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchResults = emptyList(),
                        searchError = error.localizedMessage ?: "Error al buscar por DNI"
                    )
                }
            }
        }
    }

    // --- FORM MANAGEMENT ---
    fun onFormDniChanged(dni: String) {
        if (dni.length <= 8 && dni.all { it.isDigit() }) {
            _formState.update { it.copy(dni = dni, errorMessage = null) }
        }
    }

    fun onFormTipoChanged(tipo: String) {
        _formState.update { it.copy(tipo = tipo, errorMessage = null) }
    }

    fun onFormDireccionChanged(direccion: String) {
        _formState.update { it.copy(direccion = direccion, errorMessage = null) }
    }

    fun onFormDescripcionChanged(descripcion: String) {
        _formState.update { it.copy(descripcion = descripcion, errorMessage = null) }
    }

    fun onFormFechaChanged(fecha: String) {
        _formState.update { it.copy(fecha = fecha) }
    }

    fun resetFormSuccessMessage() {
        _formState.update { it.copy(successMessage = null) }
    }

    fun resetFormErrorMessage() {
        _formState.update { it.copy(errorMessage = null) }
    }

    fun registrarDenuncia() {
        val form = _formState.value

        // Validation
        if (form.dni.trim().length != 8) {
            _formState.update { it.copy(errorMessage = "El DNI debe tener exactamente 8 dígitos.") }
            return
        }
        if (form.direccion.trim().isEmpty()) {
            _formState.update { it.copy(errorMessage = "La dirección es obligatoria.") }
            return
        }
        if (form.descripcion.trim().isEmpty()) {
            _formState.update { it.copy(errorMessage = "La descripción es obligatoria.") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, errorMessage = null, successMessage = null) }

            val nuevaDenuncia = Denuncia(
                dni = form.dni.trim(),
                fecha = if (form.fecha.isBlank()) getCurrentFormattedDate() else form.fecha,
                tipo = form.tipo,
                direccion = form.direccion.trim(),
                descripcion = form.descripcion.trim(),
                estado = 1
            )

            val result = repository.registrarDenuncia(nuevaDenuncia)
            result.onSuccess { registrada ->
                _formState.update {
                    FormState(
                        successMessage = "Denuncia #${registrada.id ?: ""} registrada con éxito.",
                        fecha = getCurrentFormattedDate()
                    )
                }
                // Refresh list
                cargarDenuncias(reset = true)
            }.onFailure { error ->
                _formState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.localizedMessage ?: "Error al registrar la denuncia."
                    )
                }
            }
        }
    }

    // --- DETAIL & ATENDER DENUNCIA ---
    fun seleccionarDenuncia(denuncia: Denuncia?) {
        _uiState.update { it.copy(selectedDenuncia = denuncia, actionMessage = null) }
    }

    fun resetActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    fun atenderDenuncia(denuncia: Denuncia) {
        val id = denuncia.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isAttending = true, actionMessage = null) }

            val result = repository.atenderDenuncia(denuncia)
            result.onSuccess { atendida ->
                _uiState.update { state ->
                    // Update state in main list
                    val listActualizada = state.denuncias.map { item ->
                        if (item.id == id) item.copy(estado = 0) else item
                    }
                    val searchActualizada = state.searchResults.map { item ->
                        if (item.id == id) item.copy(estado = 0) else item
                    }
                    val selectedActualizada = state.selectedDenuncia?.copy(estado = 0)

                    state.copy(
                        denuncias = listActualizada,
                        searchResults = searchActualizada,
                        selectedDenuncia = selectedActualizada,
                        isAttending = false,
                        actionMessage = "Denuncia #$id marcada como ATENDIDA por serenazgo."
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isAttending = false,
                        actionMessage = "Error al atender denuncia: ${error.localizedMessage}"
                    )
                }
            }
        }
    }
}
