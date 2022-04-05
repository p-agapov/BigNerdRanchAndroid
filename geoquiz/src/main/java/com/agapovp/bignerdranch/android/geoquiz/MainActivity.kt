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
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var textQuestion: TextView
    private lateinit var buttonTrue: Button
    private lateinit var buttonFalse: Button
    private lateinit var buttonPrevious: ImageButton
    private lateinit var buttonNext: ImageButton

    private val quizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java).also { quizViewModel ->
            Log.d(TAG, "Got a QuizViewModel $quizViewModel")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        setContentView(R.layout.activity_main)
        quizViewModel.setState(savedInstanceState)

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState called")
        outState.putAll(quizViewModel.getState())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    private fun updateTextQuestionText() {
        with(quizViewModel) {
            textQuestion.setText(currentQuestionText)
            setAnswerButtonsState(currentQuestionIsActive)
        }
    }

    private fun setAnswerButtonsState(isActive: Boolean) {
        buttonTrue.isEnabled = isActive
        buttonFalse.isEnabled = isActive
    }

    private fun setPreviousQuestion() {
        quizViewModel.toPreviousQuestion()
        updateTextQuestionText()
    }

    private fun setNextQuestion() {
        quizViewModel.toNextQuestion()
        updateTextQuestionText()
    }

    private fun checkAnswer(answer: Boolean) {
        quizViewModel.incrementQuestionsAnswered()
        val messageText = if (answer == quizViewModel.currentQuestionAnswer) {
            quizViewModel.incrementQuestionsAnsweredCorrectly()
            R.string.toast_true_text
        } else {
            R.string.toast_false_text
        }
        showToastTop(messageText)

        setAnswerButtonsState(false)
        quizViewModel.currentQuestionIsActive = false

        if (quizViewModel.isQuizFinished()) showScore()
    }

    private fun showScore() {
        showToastTop(getString(R.string.toast_score, quizViewModel.getScore()))
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
