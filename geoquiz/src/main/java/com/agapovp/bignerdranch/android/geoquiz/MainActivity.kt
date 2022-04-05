package com.agapovp.bignerdranch.android.geoquiz

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val questionBank = listOf(
        Question(R.string.question_australia_text, true),
        Question(R.string.question_oceans_text, true),
        Question(R.string.question_mideast_text, false),
        Question(R.string.question_africa_text, false),
        Question(R.string.question_americas_text, true),
        Question(R.string.question_asia_text, true)
    )

    private var currentIndex = 0
    private var questionAnswered: Double = 0.0
    private var questionAnsweredCorrectly = 0.0

    private lateinit var textQuestion: TextView
    private lateinit var buttonTrue: Button
    private lateinit var buttonFalse: Button
    private lateinit var buttonPrevious: ImageButton
    private lateinit var buttonNext: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_main)

        textQuestion = findViewById<TextView>(R.id.text_question).apply {
            setOnClickListener {
                setNextQuestion()
            }
        }
        buttonTrue = findViewById<Button?>(R.id.button_true).apply {
            setOnClickListener {
                checkAnswer(true)
            }
        }
        buttonFalse = findViewById<Button?>(R.id.button_false).apply {
            setOnClickListener {
                checkAnswer(false)
            }
        }
        buttonPrevious = findViewById<ImageButton?>(R.id.button_previous).apply {
            setOnClickListener {
                setPreviousQuestion()
            }
        }
        buttonNext = findViewById<ImageButton?>(R.id.button_next).apply {
            setOnClickListener {
                setNextQuestion()
            }
        }

        updateTextQuestionText()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    private fun updateTextQuestionText() {
        with(questionBank[currentIndex]) {
            textQuestion.setText(textResId)
            setAnswerButtonsState(isActive)
        }
    }

    private fun setAnswerButtonsState(isActive: Boolean) {
        buttonTrue.isEnabled = isActive
        buttonFalse.isEnabled = isActive
    }

    private fun setPreviousQuestion() {
        currentIndex = (currentIndex - 1) % questionBank.size
        if (currentIndex < 0) currentIndex = questionBank.size - 1
        updateTextQuestionText()
    }

    private fun setNextQuestion() {
        currentIndex = (currentIndex + 1) % questionBank.size
        updateTextQuestionText()
    }

    private fun checkAnswer(answer: Boolean) {
        questionAnswered++
        val messageText = if (answer == questionBank[currentIndex].answer) {
            questionAnsweredCorrectly++
            R.string.toast_true_text
        } else {
            R.string.toast_false_text
        }
        showToastTop(messageText)

        setAnswerButtonsState(false)
        questionBank[currentIndex].isActive = false

        if (questionAnswered == questionBank.size.toDouble()) showScore()
    }

    private fun showScore() {
        showToastTop(
            getString(
                R.string.toast_score,
                (questionAnsweredCorrectly / questionAnswered * 100).toInt()
            )
        )
    }

    private fun showToastTop(messageText: CharSequence) {
        Toast.makeText(this, messageText, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.TOP, 0, 250)
        }.show()
    }

    private fun showToastTop(@StringRes messageText: Int) {
        showToastTop(getString(messageText))
    }
}
