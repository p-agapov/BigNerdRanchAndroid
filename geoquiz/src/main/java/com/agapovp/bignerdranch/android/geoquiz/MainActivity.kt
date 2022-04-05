package com.agapovp.bignerdranch.android.geoquiz

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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

    private lateinit var textQuestion: TextView
    private lateinit var buttonTrue: Button
    private lateinit var buttonFalse: Button
    private lateinit var buttonPrevious: ImageButton
    private lateinit var buttonNext: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun updateTextQuestionText() {
        textQuestion.setText(questionBank[currentIndex].textResId)
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
        val messageText = if (answer == questionBank[currentIndex].answer) {
            R.string.toast_true_text
        } else {
            R.string.toast_false_text
        }
        Toast.makeText(this, messageText, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.TOP, 0, 250)
        }.show()
    }
}
