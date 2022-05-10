package com.agapovp.bignerdranch.android.criminalintent.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import kotlin.math.roundToInt

fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {

    var options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(path, options)

    val scrWidth = options.outWidth.toFloat()
    val scrHeight = options.outHeight.toFloat()

    var inSampleSize = 1
    if (scrWidth > destWidth || scrHeight > destHeight) {
        val widthScale = scrWidth / destWidth
        val heightScale = scrHeight / destHeight

        val sampleScale =
            if (widthScale > heightScale) widthScale
            else heightScale

        inSampleSize = sampleScale.roundToInt()
    }

    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize

    return getOrientedBitmap(path, options)
}

fun getOrientedBitmap(path: String, options: BitmapFactory.Options): Bitmap {

    fun rotate(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(angle)
        }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    val orientation = ExifInterface(path).getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )
    val source = BitmapFactory.decodeFile(path, options)

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotate(source, 90.0f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotate(source, 180.0f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotate(source, 270.0f)
        else -> source
    }
}
