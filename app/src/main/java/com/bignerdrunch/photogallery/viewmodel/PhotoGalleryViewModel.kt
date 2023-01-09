package com.bignerdrunch.photogallery.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdrunch.photogallery.repository.PhotoRepository
import com.bignerdrunch.photogallery.api.GalleryItem
import com.bignerdrunch.photogallery.repository.PreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel: ViewModel() {
    private val photoRepository = PhotoRepository()

    /**
     * get a PreferencesRepository's object
     */
    private val preferencesRepository = PreferencesRepository.get()

    private val _uiState: MutableStateFlow<PhotoGalleryUiState> = MutableStateFlow(PhotoGalleryUiState())
    val uiState: StateFlow<PhotoGalleryUiState>
        get() = _uiState.asStateFlow()

    init {

        viewModelScope.launch {

            /**
             * listening the storedQuery, which returns a new value from the file 'settings'
             * collectLatest{} - return the last query, cancels the previous query
             */
            preferencesRepository.storedQuery.collectLatest { storedQuery ->
                try {
//                val items = photoRepository.fetchPhotos()
//                Log.d(TAG, "Item received $items")

                    val items = fetchGalleryItems(storedQuery)
                    _uiState.update { oldState ->
                        oldState.copy(
                            images = items,
                            query = storedQuery
                        )
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "Filed to fetch gallery items", ex)
                }
            }
        }

        /**
         * polling state research
         */
        viewModelScope.launch {
            preferencesRepository.isPolling.collect { isPolling ->
                _uiState.update { it.copy(isPolling = isPolling) }
            }
        }
    }

    /**
     * set a new query to the file 'settings' using preferencesRepository
     */
    fun setQuery(query: String) {
        viewModelScope.launch { preferencesRepository.setStoredQuery(query) }
    }

    /**
     * Change the polling state in the file
     */
    fun toggleIsPolling() {
        viewModelScope.launch {
            preferencesRepository.setPolling(!uiState.value.isPolling)
        }
    }

    /**
     * call the functions from FlickrApi class
     */
    private suspend fun fetchGalleryItems(query: String): List<GalleryItem> {
        return if (query.isNotEmpty()) {
            photoRepository.searchPhotos(query)
        } else {
            photoRepository.fetchPhotos()
        }
    }
 }

/**
 * Handle the UI state
 */
data class PhotoGalleryUiState(
    val images: List<GalleryItem> = listOf(),
    val query: String = "",
    val isPolling: Boolean = false
)