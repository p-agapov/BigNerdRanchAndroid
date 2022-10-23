package com.agapovp.bignerdranch.android.photogallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PhotoPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo_page)

        supportFragmentManager.findFragmentById(R.id.activity_photo_page_container)
            ?: supportFragmentManager.beginTransaction()
                .add(R.id.activity_photo_page_container, PhotoPageFragment.newInstance(intent.data))
                .commit()
    }

    companion object {

        @JvmStatic
        fun newIntent(context: Context, photoPageUri: Uri): Intent =
            Intent(context, PhotoPageActivity::class.java).apply {
                data = photoPageUri
            }
    }
}
