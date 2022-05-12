package com.agapovp.bignerdranch.android.beatbox

import android.content.res.AssetManager
import android.os.Bundle
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agapovp.bignerdranch.android.beatbox.databinding.ActivityMainBinding
import com.agapovp.bignerdranch.android.beatbox.databinding.ItemSoundBinding

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(
            this,
            MainActivityViewModelFactory(assets, countRate(PROGRESS_START))
        ).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityMainBinding?>(this, R.layout.activity_main).apply {
            activityMainRecyclerviewSounds.apply {
                layoutManager = GridLayoutManager(context, SPAN_COUNT)
                adapter = SoundItemAdapter(viewModel.beatBox.sounds)
            }
            activityMainTextPlaybackSpeed.text = getString(
                R.string.activity_main_text_playback_speed_text,
                countRate(PROGRESS_START).toString()
            )
            activityMainSeekbarPlaybackSpeed.apply {
                max = PROGRESS_MAX
                progress = PROGRESS_START
                setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?, progress: Int, fromUser: Boolean
                    ) {
                        val rate = countRate(progress)
                        viewModel.beatBox.rate = rate
                        activityMainTextPlaybackSpeed.text = getString(
                            R.string.activity_main_text_playback_speed_text,
                            rate.toString()
                        )
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                }
                )
            }
        }
    }

    private inner class SoundItemHolder(private val binding: ItemSoundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = SoundItemViewModel(viewModel.beatBox)
        }

        fun bind(sound: Sound) {
            binding.apply {
                viewModel?.sound = sound
                executePendingBindings()
            }
        }
    }

    private inner class SoundItemAdapter(private val sounds: List<Sound>) :
        RecyclerView.Adapter<SoundItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundItemHolder =
            SoundItemHolder(
                DataBindingUtil.inflate<ItemSoundBinding?>(
                    layoutInflater,
                    R.layout.item_sound,
                    parent,
                    false
                ).apply {
                    lifecycleOwner = this@MainActivity
                }
            )

        override fun onBindViewHolder(holder: SoundItemHolder, position: Int) {
            holder.bind(sounds[position])
        }

        override fun getItemCount(): Int = sounds.size
    }

    private fun countRate(progress: Int): Float =
        when (progress) {
            in 0..11 -> 0.25F * (progress + 1)
            else -> progress - 8.0F
        }

    private class MainActivityViewModelFactory(
        private val assets: AssetManager,
        private val rate: Float
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(AssetManager::class.java, Float::class.java)
                .newInstance(assets, rate)
        }
    }

    companion object {

        private const val TAG = "MainActivity"

        private const val SPAN_COUNT = 3
        private const val PROGRESS_MAX = 13
        private const val PROGRESS_START = 3
    }
}
