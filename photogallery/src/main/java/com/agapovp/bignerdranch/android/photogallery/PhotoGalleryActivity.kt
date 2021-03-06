package com.agapovp.bignerdranch.android.photogallery

import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.enableDefaults()
        setContentView(R.layout.activity_photo_gallery)

        savedInstanceState ?: run {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.activity_photo_gallery_container, PhotoGalleryFragment.newInstance())
                .commit()
        }
    }
}
