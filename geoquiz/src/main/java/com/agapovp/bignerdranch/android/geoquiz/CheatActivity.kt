package com.agapovp.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible

private const val TAG = "CheatActivity"

private const val KEY_CURRENT_QUESTION_IS_ACTIVE = "${TAG}_KEY_CURRENT_QUESTION_IS_ACTIVE"
private const val KEY_CURRENT_QUESTION_IS_CHEATED = "${TAG}_KEY_CURRENT_QUESTION_IS_CHEATED"

class CheatActivity : AppCompatActivity() {

    private var currentQuestionIsActive: Boolean = true
    private var currentQuestionIsCheated: Boolean = false
    private val isTextAnswerVisible: Boolean
        get() = !currentQuestionIsActive || currentQuestionIsCheated

    private lateinit var textAnswer: TextView
    private lateinit var textApiVersion: TextView
    private lateinit var buttonShowAnswer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cheat)

        currentQuestionIsActive = intent.getBooleanExtra(EXTRA_CURRENT_QUESTION_IS_ACTIVE, true)
        currentQuestionIsCheated = intent.getBooleanExtra(EXTRA_CURRENT_QUESTION_IS_CHEATED, false)

        savedInstanceState?.getBoolean(KEY_CURRENT_QUESTION_IS_ACTIVE)?.let {
            currentQuestionIsActive = it
        }
        savedInstanceState?.getBoolean(KEY_CURRENT_QUESTION_IS_CHEATED)?.let {
            currentQuestionIsCheated = it
        }

        setResultCurrentQuestionIsCheated(currentQuestionIsCheated)

        textAnswer = findViewById<TextView>(R.id.activity_cheat_text_answer).apply {
            show(isTextAnswerVisible)
            text = getString(
                R.string.activity_cheat_text_answer_text,
                intent.getBooleanExtra(EXTRA_CURRENT_QUESTION_ANSWER, false)
            )
        }
        textApiVersion = findViewById<TextView>(R.id.activity_cheat_text_api_version).apply {
            text = getString(R.string.activity_cheat_text_api_version_text, SDK_INT)
        }
        buttonShowAnswer = findViewById<Button>(R.id.activity_cheat_button_show_answer).apply {
            isEnabled = !isTextAnswerVisible
            setOnClickListener {
                currentQuestionIsCheated = true
                isEnabled = !isTextAnswerVisible
                textAnswer.isVisible = isTextAnswerVisible

                setResultCurrentQuestionIsCheated(currentQuestionIsCheated)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(
            bundleOf(
                KEY_CURRENT_QUESTION_IS_ACTIVE to currentQuestionIsActive,
                KEY_CURRENT_QUESTION_IS_CHEATED to currentQuestionIsCheated
            )
        )
    }

    private fun setResultCurrentQuestionIsCheated(isCheated: Boolean) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(EXTRA_CURRENT_QUESTION_IS_CHEATED, isCheated)
        })
    }

    companion object {
        const val EXTRA_CURRENT_QUESTION_IS_CHEATED = "${TAG}_EXTRA_CURRENT_QUESTION_IS_CHEATED"

        private const val EXTRA_CURRENT_QUESTION_ANSWER = "${TAG}_EXTRA_CURRENT_QUESTION_ANSWER"
        private const val EXTRA_CURRENT_QUESTION_IS_ACTIVE = "${TAG}_EXTRA_CURRENT_QUESTION_IS_ACTIVE"

        fun newIntent(
            packageContext: Context,
            currentQuestionAnswer: Boolean,
            currentQuestionIsActive: Boolean,
            currentQuestionIsCheated: Boolean
        ) =
            Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_CURRENT_QUESTION_ANSWER, currentQuestionAnswer)
                putExtra(EXTRA_CURRENT_QUESTION_IS_ACTIVE, currentQuestionIsActive)
                putExtra(EXTRA_CURRENT_QUESTION_IS_CHEATED, currentQuestionIsCheated)
            }

        private fun View.show(show: Boolean) {
            visibility = if (show) View.VISIBLE else View.INVISIBLE
        }
    }
}
