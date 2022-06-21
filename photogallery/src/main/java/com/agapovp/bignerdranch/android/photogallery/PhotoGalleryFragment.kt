package com.agapovp.bignerdranch.android.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PhotoGalleryFragment : Fragment() {

    private lateinit var layoutManager: GridLayoutManager
    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    private val viewModel: PhotoGalleryViewModel by lazy {
        ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutManager = GridLayoutManager(context, DEFAULT_SPAN_COUNT)

        retainInstance = true

        val responseHandler = Handler(Looper.getMainLooper())
        thumbnailDownloader = ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
            Log.d(TAG, "onCreate: ${Thread.currentThread()}")
            photoHolder.bindDrawable(BitmapDrawable(resources, bitmap))
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        viewLifecycleOwnerLiveData.observe(viewLifecycleOwner) { lifecycleOwner ->
//            lifecycleOwner?.let {
//                viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)
//            }
//        }

        return inflater.inflate(R.layout.fragment_photo_gallery, container, false)?.also { view ->
            recyclerViewPhotos =
                view.findViewById<RecyclerView?>(R.id.fragment_photo_gallery_recyclerview_photos)
                    .apply {
                        layoutManager = this@PhotoGalleryFragment.layoutManager
                        viewTreeObserver.addOnGlobalLayoutListener {
                            (layoutManager as GridLayoutManager).spanCount =
                                requireContext().resources.displayMetrics.let { dm ->
                                    dm.widthPixels / dm.density.toInt()
                                } / DEFAULT_SPAN_WIDTH
                        }
                    }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagingAdapter = PhotoAdapter(GalleryItemComparator)
        recyclerViewPhotos.adapter = pagingAdapter

        lifecycleScope.launch {
            viewModel.galleryItems.collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

//        viewLifecycleOwner.lifecycle.removeObserver(thumbnailDownloader.viewLifecycleObserver)

        thumbnailDownloader.clearQueue()
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    private class PhotoHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView) {

        val bindDrawable: (Drawable) -> Unit = itemView::setImageDrawable
    }

    private inner class PhotoAdapter(diffCallback: DiffUtil.ItemCallback<GalleryItem>) :
        PagingDataAdapter<GalleryItem, PhotoHolder>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder =
            PhotoHolder(layoutInflater.inflate(R.layout.item_gallery, parent, false) as ImageView)

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bindDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.bill_up_close)
                    ?: ColorDrawable()
            )

            thumbnailDownloader.queueThumbnail(holder, getItem(position)?.url)

            val first = layoutManager.findFirstCompletelyVisibleItemPosition()
            for (i in first - 1 downTo first - DEFAULT_PRELOAD_COUNT) {
                if (i > 0) {
                    thumbnailDownloader.preloadThumbnail(getItem(i)?.url)
                }
            }
            val last = layoutManager.findLastCompletelyVisibleItemPosition()
            for (i in last + 1..last + DEFAULT_PRELOAD_COUNT) {
                if (i > 0) {
                    thumbnailDownloader.preloadThumbnail(getItem(i)?.url)
                }
            }
        }
    }

    private object GalleryItemComparator : DiffUtil.ItemCallback<GalleryItem>() {
        override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {

        private const val TAG = "PhotoGalleryFragment"

        private const val DEFAULT_SPAN_COUNT = 3
        private const val DEFAULT_SPAN_WIDTH = 120

        private const val DEFAULT_PRELOAD_COUNT = 10

        @JvmStatic
        fun newInstance() =
            PhotoGalleryFragment().apply {
                arguments = bundleOf(

                )
            }
    }
}
