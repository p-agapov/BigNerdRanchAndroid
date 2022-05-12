package com.agapovp.bignerdranch.android.beatbox

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agapovp.bignerdranch.android.beatbox.databinding.ActivityMainBinding
import com.agapovp.bignerdranch.android.beatbox.databinding.ItemSoundBinding

class MainActivity : AppCompatActivity() {

    private lateinit var beatBox: BeatBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        beatBox = BeatBox(assets)

        DataBindingUtil.setContentView<ActivityMainBinding?>(this, R.layout.activity_main).apply {
            activityMainRecyclerviewSounds.apply {
                layoutManager = GridLayoutManager(context, SPAN_COUNT)
                adapter = SoundItemAdapter(beatBox.sounds)
            }
        }
    }

    private inner class SoundItemHolder(private val binding: ItemSoundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = SoundItemViewModel()
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

    companion object {

        private const val TAG = "MainActivity"

        private const val SPAN_COUNT = 3
    }
}
