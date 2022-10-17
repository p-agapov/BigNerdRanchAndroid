package com.agapovp.bignerdranch.android.photogallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class PhotoGalleryViewModel(
    app: Application
) : AndroidViewModel(app) {

    private val repository: FlickrRepository = FlickrRepository()

    private val mutableSearchTerm: MutableStateFlow<String> =
        MutableStateFlow(QueryPreferences.getStoredQuery(getApplication()))

    private val _isProgressVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val searchTerm: String
        get() = mutableSearchTerm.value

    val galleryItems = mutableSearchTerm.flatMapLatest { searchTerm ->
        Pager(PagingConfig(PAGE_SIZE)) {
            GalleryItemPagingSource(repository, searchTerm)
        }.flow.cachedIn(viewModelScope)
    }

    val isProgressVisible: StateFlow<Boolean> = _isProgressVisible.asStateFlow()

    fun fetchPhotos(query: String) {
        QueryPreferences.setStoredQuery(getApplication(), query)
        mutableSearchTerm.value = query
    }

    fun setProgress(isProgressVisible: Boolean) {
        _isProgressVisible.value = isProgressVisible
    }

    companion object {

        private const val PAGE_SIZE = 100
    }
}
