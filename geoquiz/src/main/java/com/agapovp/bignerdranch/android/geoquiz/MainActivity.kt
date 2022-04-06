package com.agapovp.bignerdranch.android.geoquiz

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.agapovp.bignerdranch.android.geoquiz.CheatActivity.Companion.EXTRA_CURRENT_QUESTION_IS_CHEATED

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val cheatActivityLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                quizViewModel.currentQuestionIsCheated =
                    result.data?.getBooleanExtra(EXTRA_CURRENT_QUESTION_IS_CHEATED, false) ?: false
            }
        }

    private lateinit var textQuestion: TextView
    private lateinit var buttonTrue: Button
    private lateinit var buttonFalse: Button
    private lateinit var buttonCheat: Button
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

        textQuestion = findViewById<TextView>(R.id.main_activity_text_question).apply {
            setOnClickListener {
                setNextQuestion()
            }
        }
        buttonTrue = findViewById<Button?>(R.id.main_activity_button_true).apply {
            setOnClickListener {
                checkAnswer(true)
            }
        }
        buttonFalse = findViewById<Button?>(R.id.main_activity_button_false).apply {
            setOnClickListener {
                checkAnswer(false)
            }
        }
        buttonCheat = findViewById<Button?>(R.id.main_activity_button_cheat).also { button ->
            button.setOnClickListener {
                cheatActivityLauncher.launch(
                    CheatActivity.newIntent(
                        this,
                        quizViewModel.currentQuestionAnswer,
                        quizViewModel.currentQuestionIsCheated || !quizViewModel.currentQuestionIsActive
                    )
                )
            }
        }
        buttonPrevious = findViewById<ImageButton?>(R.id.main_activity_button_previous).apply {
            setOnClickListener {
                setPreviousQuestion()
            }
        }
        buttonNext = findViewById<ImageButton?>(R.id.main_activity_button_next).apply {
            setOnClickListener {
                setNextQuestion()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")

        updateTextQuestionText()
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
        val messageText = when {
            quizViewModel.currentQuestionIsCheated -> R.string.main_activity_toast_judgment_text
            answer == quizViewModel.currentQuestionAnswer -> {
                quizViewModel.incrementQuestionsAnsweredCorrectly()
                R.string.main_activity_toast_true_text
            }
            else -> R.string.main_activity_toast_false_text
        }
        showToastTop(messageText)

        setAnswerButtonsState(false)
        quizViewModel.currentQuestionIsActive = false

        if (quizViewModel.isQuizFinished()) showScore()
    }

    private fun showScore() {
        showToastTop(getString(R.string.main_activity_toast_score, quizViewModel.getScore()))
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
