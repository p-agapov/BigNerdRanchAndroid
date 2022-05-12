package com.agapovp.bignerdranch.android.beatbox

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SoundItemViewModelTest {

    private lateinit var beatBox: BeatBox
    private lateinit var sound: Sound
    private lateinit var subject: SoundItemViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        beatBox = mock(BeatBox::class.java)
        sound = Sound("assetPath")
        subject = SoundItemViewModel(beatBox)
        subject.sound = sound
    }

    @Test
    fun `Exposes sound name as title`() {
        assertThat(subject.title.value, `is`(sound.name))
    }

    @Test
    fun `Calls BeatBox play on button clicked`() {
        subject.onButtonClicked()
        verify(beatBox).play(sound)
    }
}
