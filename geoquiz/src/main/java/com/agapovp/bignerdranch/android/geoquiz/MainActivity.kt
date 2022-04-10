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
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.agapovp.bignerdranch.android.geoquiz.CheatActivity.Companion.EXTRA_CURRENT_QUESTION_IS_CHEATED

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val cheatActivityLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.currentQuestionIsCheated =
                    result.data?.getBooleanExtra(EXTRA_CURRENT_QUESTION_IS_CHEATED, false) ?: false
            }
        }

    private lateinit var textQuestion: TextView
    private lateinit var textNumberOfHints: TextView
    private lateinit var buttonTrue: Button
    private lateinit var buttonFalse: Button
    private lateinit var buttonCheat: Button
    private lateinit var buttonPrevious: ImageButton
    private lateinit var buttonNext: ImageButton

    private val viewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java).also { quizViewModel ->
            Log.d(TAG, "Got a QuizViewModel $quizViewModel")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        setContentView(R.layout.activity_main)
        viewModel.setState(savedInstanceState)

        textQuestion = findViewById<TextView>(R.id.activity_main_text_question).apply {
            setOnClickListener {
                setNextQuestion()
            }
        }
        buttonTrue = findViewById<Button>(R.id.activity_main_button_true).apply {
            setOnClickListener {
                checkAnswer(true)
            }
        }
        buttonFalse = findViewById<Button>(R.id.activity_main_button_false).apply {
            setOnClickListener {
                checkAnswer(false)
            }
        }
        buttonCheat = findViewById<Button>(R.id.activity_main_button_cheat).also { button ->
            button.setOnClickListener {
                cheatActivityLauncher.launch(
                    CheatActivity.newIntent(
                        this,
                        viewModel.currentQuestionAnswer,
                        viewModel.currentQuestionIsActive,
                        viewModel.currentQuestionIsCheated
                    ),
                    ActivityOptionsCompat.makeClipRevealAnimation(
                        button,
                        0,
                        0,
                        button.width,
                        button.height
                    )
                )
            }
        }
        textNumberOfHints = findViewById(R.id.activity_main_text_number_of_hints)
        buttonPrevious = findViewById<ImageButton>(R.id.activity_main_button_previous).apply {
            setOnClickListener {
                setPreviousQuestion()
            }
        }
        buttonNext = findViewById<ImageButton?>(R.id.activity_main_button_next).apply {
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
        outState.putAll(viewModel.getState())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    private fun updateTextQuestionText() {
        with(viewModel) {
            textQuestion.setText(currentQuestionText)
            setAnswerButtonsState(currentQuestionIsActive)
            textNumberOfHints.text = getString(
                R.string.activity_main_text_number_of_hints_text,
                viewModel.numberOfHints.first,
                viewModel.numberOfHints.second
            )
            buttonCheat.isEnabled =
                (viewModel.numberOfHints.first != viewModel.numberOfHints.second)
                        && !viewModel.isQuizFinished()
        }
    }

    private fun setAnswerButtonsState(isActive: Boolean) {
        buttonTrue.isEnabled = isActive
        buttonFalse.isEnabled = isActive
    }

    private fun setPreviousQuestion() {
        viewModel.toPreviousQuestion()
        updateTextQuestionText()
    }

    private fun setNextQuestion() {
        viewModel.toNextQuestion()
        updateTextQuestionText()
    }

    private fun checkAnswer(answer: Boolean) {
        viewModel.incrementQuestionsAnswered()
        val messageText = when {
            viewModel.currentQuestionIsCheated -> R.string.activity_main_toast_judgment_text
            answer == viewModel.currentQuestionAnswer -> {
                viewModel.incrementQuestionsAnsweredCorrectly()
                R.string.activity_main_toast_true_text
            }
            else -> R.string.activity_main_toast_false_text
        }
        showToastTop(messageText)

        setAnswerButtonsState(false)
        viewModel.currentQuestionIsActive = false

        if (viewModel.isQuizFinished()) {
            buttonCheat.isEnabled = false
            showScore()
        }
    }

    private fun showScore() {
        showToastTop(getString(R.string.activity_main_toast_score, viewModel.getScore()))
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
