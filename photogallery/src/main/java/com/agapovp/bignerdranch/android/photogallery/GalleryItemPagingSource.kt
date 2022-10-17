package com.agapovp.bignerdranch.android.photogallery

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState


class GalleryItemPagingSource(
    private val flickrRepository: FlickrRepository,
    private val query: String
) : PagingSource<Int, GalleryItem>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        return try {
            val nextPageNumber: Int = params.key ?: 1

            suspend fun getData() =
                if (query.isBlank()) flickrRepository.fetchPhotos(nextPageNumber)
                else flickrRepository.searchPhotos(query, nextPageNumber)

            LoadResult.Page(
                data = getData(),
                prevKey = null,
                nextKey = nextPageNumber + 1
            )
        } catch (t: Throwable) {
            Log.e(TAG, "Failure to fetch photos", t)
            LoadResult.Error(t)
        }
    }

    companion object {

        private const val TAG = "GalleryItemPagingSource"
    }
}
