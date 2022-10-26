package com.agapovp.bignerdranch.android.sunset

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val blueSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.blue_sky)
    }
    private val sunsetSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.sunset_sky)
    }
    private val nightSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.night_sky)
    }

    private val heightAnimator: ObjectAnimator by lazy {
        ObjectAnimator
            .ofFloat(sunView, "y", sunView.top.toFloat(), skyView.height.toFloat())
            .setDuration(3000)
            .apply {
                interpolator = LinearInterpolator()
            }
    }

    private val sunsetSkyAnimator: ObjectAnimator by lazy {
        ObjectAnimator
            .ofInt(skyView, "backgroundColor", blueSkyColor, sunsetSkyColor)
            .setDuration(3000)
            .apply {
                doOnEnd { state.doOnEnd() }
                setEvaluator(ArgbEvaluator())
            }
    }

    private val reflectionAnimator: ObjectAnimator by lazy {
        ObjectAnimator
            .ofFloat(reflectionView, "y", reflectionView.top.toFloat(), -reflectionView.height.toFloat())
            .setDuration(3000)
            .apply {
                interpolator = LinearInterpolator()
            }
    }

    private val nightSkyAnimator: ObjectAnimator by lazy {
        ObjectAnimator
            .ofInt(skyView, "backgroundColor", sunsetSkyColor, nightSkyColor)
            .setDuration(1500)
            .apply {
                doOnEnd { state.doOnEnd() }
                setEvaluator(ArgbEvaluator())
            }
    }

    private var state: State = Day()

    private lateinit var sceneView: View
    private lateinit var skyView: View
    private lateinit var sunView: View
    private lateinit var brightRays: View
    private lateinit var darkRays: View
    private lateinit var seaView: View
    private lateinit var reflectionView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById<View?>(R.id.activity_main_scene).apply {
            setOnClickListener { state.onClick() }
        }
        skyView = findViewById(R.id.activity_main_sky)
        sunView = findViewById(R.id.activity_main_sun)
        brightRays = findViewById(R.id.activity_main_bright_rays)
        darkRays = findViewById(R.id.activity_main_dark_rays)
        seaView = findViewById(R.id.activity_main_sea)
        reflectionView = findViewById(R.id.activity_main_reflection)

        startRaysAnimation()
    }

    private fun startRaysAnimation() {

        val brightRaysX = PropertyValuesHolder.ofFloat("scaleX", 1F, 0.9F, 1F)
        val brightRaysY = PropertyValuesHolder.ofFloat("scaleY", 1F, 0.9F, 1F)
        val darkRaysX = PropertyValuesHolder.ofFloat("scaleX", 1F, 1.1F, 1F)
        val darkRaysY = PropertyValuesHolder.ofFloat("scaleY", 1F, 1.1F, 1F)

        ObjectAnimator
            .ofPropertyValuesHolder(brightRays, brightRaysX, brightRaysY)
            .setDuration(3000)
            .apply {
                interpolator = LinearInterpolator()
                repeatCount = ValueAnimator.INFINITE
            }.start()

        ObjectAnimator
            .ofPropertyValuesHolder(darkRays, darkRaysX, darkRaysY)
            .setDuration(1500)
            .apply {
                interpolator = LinearInterpolator()
                repeatCount = ValueAnimator.INFINITE
            }.start()

        ObjectAnimator
            .ofFloat(brightRays, "rotation", 0F, 360F)
            .setDuration(20000)
            .apply {
                interpolator = LinearInterpolator()
                repeatCount = ValueAnimator.INFINITE
            }.start()

        ObjectAnimator
            .ofFloat(darkRays, "rotation", 0F, -360F)
            .setDuration(20000)
            .apply {
                interpolator = LinearInterpolator()
                repeatCount = ValueAnimator.INFINITE
            }.start()
    }

    private abstract class State {

        protected var isReversed = false

        init {
            Log.d(TAG, "Switched to state:${javaClass.simpleName}")
        }

        @CallSuper
        open fun switch(reverse: Boolean) {
            isReversed = reverse
        }

        @CallSuper
        open fun onClick() {
            isReversed = !isReversed
        }

        abstract fun doOnEnd()
    }

    private inner class Day : State() {

        override fun onClick() {
            super.onClick()

            state = Sun().apply { switch(false) }
        }

        override fun doOnEnd() {}
    }

    private inner class Sun : State() {

        override fun switch(reverse: Boolean) {
            super.switch(reverse)

            if (isReversed) {
                heightAnimator.reverse()
                sunsetSkyAnimator.reverse()
                reflectionAnimator.reverse()
            } else {
                heightAnimator.start()
                sunsetSkyAnimator.start()
                reflectionAnimator.start()
            }
        }

        override fun onClick() {
            super.onClick()

            heightAnimator.reverse()
            sunsetSkyAnimator.reverse()
            reflectionAnimator.reverse()
        }

        override fun doOnEnd() {
            state =
                if (isReversed) Day()
                else Sky().apply { switch(false) }
        }
    }

    private inner class Sky : State() {

        override fun switch(reverse: Boolean) {
            super.switch(reverse)

            if (isReversed) {
                nightSkyAnimator.reverse()
            } else {
                nightSkyAnimator.start()
            }
        }

        override fun onClick() {
            super.onClick()

            nightSkyAnimator.reverse()
        }

        override fun doOnEnd() {
            state =
                if (isReversed) Sun().apply { switch(true) }
                else Night()
        }
    }

    private inner class Night : State() {

        override fun onClick() {
            super.onClick()

            state = Sky().apply { switch(true) }
        }

        override fun doOnEnd() {}
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
