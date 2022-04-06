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

private const val KEY_IS_TEXT_ANSWER_VISIBLE = "${TAG}_KEY_IS_TEXT_ANSWER_VISIBLE"

class CheatActivity : AppCompatActivity() {

    private var isTextAnswerVisible = false

    private lateinit var textAnswer: TextView
    private lateinit var textApiVersion: TextView
    private lateinit var buttonShowAnswer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cheat)

        isTextAnswerVisible = intent.getBooleanExtra(EXTRA_CURRENT_QUESTION_IS_CHEATED, false)

        savedInstanceState?.let { bundle ->
            isTextAnswerVisible = bundle.getBoolean(KEY_IS_TEXT_ANSWER_VISIBLE)
        }

        if (isTextAnswerVisible) setResultCurrentQuestionIsCheated(isTextAnswerVisible)

        textAnswer = findViewById<TextView>(R.id.cheat_activity_text_answer).apply {
            show(isTextAnswerVisible)
            text = getString(
                R.string.cheat_activity_text_answer_text,
                intent.getBooleanExtra(EXTRA_CURRENT_QUESTION_ANSWER, false)
            )
        }
        textApiVersion = findViewById<TextView>(R.id.cheat_activity_text_api_version).apply {
            text = getString(R.string.cheat_activity_text_api_version_text, SDK_INT)
        }
        buttonShowAnswer = findViewById<Button>(R.id.cheat_activity_button_show_answer).apply {
            isEnabled = !isTextAnswerVisible
            setOnClickListener {
                isTextAnswerVisible = true
                isEnabled = !isTextAnswerVisible
                textAnswer.isVisible = isTextAnswerVisible

                setResultCurrentQuestionIsCheated(isTextAnswerVisible)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(
            bundleOf(
                KEY_IS_TEXT_ANSWER_VISIBLE to isTextAnswerVisible
            )
        )
    }

    private fun setResultCurrentQuestionIsCheated(isVisible: Boolean) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(EXTRA_CURRENT_QUESTION_IS_CHEATED, isVisible)
        })
    }

    companion object {
        const val EXTRA_CURRENT_QUESTION_ANSWER = "${TAG}_EXTRA_CURRENT_QUESTION_ANSWER"
        const val EXTRA_CURRENT_QUESTION_IS_CHEATED = "${TAG}_EXTRA_CURRENT_QUESTION_IS_CHEATED"

        fun newIntent(
            packageContext: Context,
            currentQuestionAnswer: Boolean,
            currentQuestionIsCheated: Boolean
        ) =
            Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_CURRENT_QUESTION_ANSWER, currentQuestionAnswer)
                putExtra(EXTRA_CURRENT_QUESTION_IS_CHEATED, currentQuestionIsCheated)
            }

        private fun View.show(show: Boolean) {
            visibility = if (show) View.VISIBLE else View.INVISIBLE
        }
    }
}
