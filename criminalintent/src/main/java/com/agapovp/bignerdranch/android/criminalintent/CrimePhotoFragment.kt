package com.agapovp.bignerdranch.android.criminalintent

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.agapovp.bignerdranch.android.criminalintent.utils.getScaledBitmap

class CrimePhotoFragment : DialogFragment() {

    private lateinit var filePath: String

    private lateinit var imagePhoto: ImageView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        filePath = arguments?.getString(ARG_PATH) as String

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_photo_crime, container, false).also { view ->
        imagePhoto = view.findViewById(R.id.fragment_photo_crime_image_photo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics = requireContext().resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()
        val height = (displayMetrics.heightPixels * 0.95).toInt()

        imagePhoto.setImageBitmap(getScaledBitmap(filePath, width, height))
    }

    companion object {

        private const val TAG = "CrimePhotoFragment"
        private const val ARG_PATH = "${TAG}_ARG_PATH"

        fun newInstance(path: String): CrimePhotoFragment =
            CrimePhotoFragment().apply {
                arguments = bundleOf(
                    ARG_PATH to path
                )
            }
    }
}
