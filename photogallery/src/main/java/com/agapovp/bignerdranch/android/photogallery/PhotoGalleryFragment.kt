package com.agapovp.bignerdranch.android.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PhotoGalleryFragment : Fragment() {

    private lateinit var layoutManager: GridLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    private val viewModel: PhotoGalleryViewModel by lazy {
        ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
        setHasOptionsMenu(true)

        val responseHandler = Handler(Looper.getMainLooper())
        thumbnailDownloader = ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
            photoHolder.bindDrawable(BitmapDrawable(resources, bitmap))
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_fragment_photo_gallery_item_search)
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(TAG, "SearchView QueryTextSubmit: $query")
                    viewModel.fetchPhotos(query)
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    Log.d(TAG, "SearchView QueryTextChange: $newText")
                    return false
                }
            })

            setOnSearchClickListener {
                Log.d(TAG, "SearchView SearchClick")
                setQuery(viewModel.searchTerm, false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_fragment_photo_gallery_item_clear_search -> {
            viewModel.fetchPhotos("")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
//        viewLifecycleOwnerLiveData.observe(viewLifecycleOwner) { lifecycleOwner ->
//            lifecycleOwner?.let {
//                viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)
//            }
//        }

        return inflater.inflate(R.layout.fragment_photo_gallery, container, false)?.also { view ->
            progressBar = view.findViewById(R.id.fragment_photo_gallery_progressbar)
            recyclerViewPhotos =
                view.findViewById<RecyclerView?>(R.id.fragment_photo_gallery_recyclerview_photos)
                    .apply {
                        this@PhotoGalleryFragment.layoutManager =
                            GridLayoutManager(context, DEFAULT_SPAN_COUNT)
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

        val pagingAdapter = PhotoAdapter(GalleryItemComparator).apply {
            addLoadStateListener { loadState ->
                when (loadState.refresh) {
                    is LoadState.NotLoading -> viewModel.setProgress(false)
                    is LoadState.Loading -> viewModel.setProgress(true)
                    is LoadState.Error -> Toast.makeText(
                        requireContext(),
                        R.string.fragment_photo_gallery_toast_error_text,
                        Toast.LENGTH_LONG
                    ).show()
                }
                when (loadState.append) {
                    is LoadState.Loading -> Toast.makeText(
                        requireContext(),
                        R.string.fragment_photo_gallery_toast_loading_text,
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> {}
                }
            }
        }
        recyclerViewPhotos.adapter = pagingAdapter

        viewModel.isProgressVisible.onEach { isProgressVisible ->
            progressBar.isVisible = isProgressVisible
            recyclerViewPhotos.isVisible = !isProgressVisible
        }.launchIn(lifecycleScope)

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
            Log.d(TAG, "Item position: $position")

            thumbnailDownloader.queueThumbnail(holder, getItem(position)?.url)
            thumbnailDownloader.resetPreload()

            val first = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (first != RecyclerView.NO_POSITION) {
                for (i in first - 1 downTo first - DEFAULT_PRELOAD_COUNT) {
                    Log.d(TAG, "Preload before: $i")
                    if (i in 1 until itemCount) {
                        thumbnailDownloader.preloadThumbnail(getItem(i)?.url)
                    }
                }
            }
            val last = layoutManager.findLastCompletelyVisibleItemPosition()
            if (last != RecyclerView.NO_POSITION) {
                for (i in last + 1..last + DEFAULT_PRELOAD_COUNT) {
                    Log.d(TAG, "Preload after: $i")
                    if (i in 1 until itemCount) {
                        thumbnailDownloader.preloadThumbnail(getItem(i)?.url)
                    }
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
        fun newInstance() = PhotoGalleryFragment()
    }
}
