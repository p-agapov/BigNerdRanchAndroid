package com.agapovp.bignerdranch.android.photogallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    private lateinit var recyclerViewPhotos: RecyclerView

    private val viewModel: PhotoGalleryViewModel by lazy {
        ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_photo_gallery, container, false).also { view ->
            recyclerViewPhotos =
                view.findViewById<RecyclerView?>(R.id.fragment_photo_gallery_recyclerview_photos)
                    .apply {
                        layoutManager = GridLayoutManager(context, DEFAULT_SPAN_COUNT)
                        viewTreeObserver.addOnGlobalLayoutListener {
                            (layoutManager as GridLayoutManager).spanCount =
                                requireContext().resources.displayMetrics.let { dm ->
                                    dm.widthPixels / dm.density.toInt()
                                } / DEFAULT_SPAN_WIDTH
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

    private class PhotoHolder(itemView: TextView) : RecyclerView.ViewHolder(itemView) {

        val bindTitle: (CharSequence) -> Unit = itemView::setText
    }

    private class PhotoAdapter(diffCallback: DiffUtil.ItemCallback<GalleryItem>) :
        PagingDataAdapter<GalleryItem, PhotoHolder>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder =
            PhotoHolder(TextView(parent.context))

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) =
            holder.bindTitle("$position ${getItem(position)?.title.orEmpty()}")
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

        @JvmStatic
        fun newInstance() =
            PhotoGalleryFragment().apply {
                arguments = bundleOf(

                )
            }
    }
}
