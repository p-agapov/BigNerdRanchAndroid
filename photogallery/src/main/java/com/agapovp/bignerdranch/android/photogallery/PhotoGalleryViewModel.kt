package com.agapovp.bignerdranch.android.photogallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class PhotoGalleryViewModel : ViewModel() {

    private val repository: FlickrRepository = FlickrRepository()

    val galleryItems = Pager(PagingConfig(pageSize = 100)) {
        GalleryItemPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
}
