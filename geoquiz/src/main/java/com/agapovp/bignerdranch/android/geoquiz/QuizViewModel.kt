package com.agapovp.bignerdranch.android.geoquiz

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

private const val KEY_CURRENT_INDEX = "${TAG}_KEY_CURRENT_INDEX"
private const val KEY_QUESTIONS_ANSWERED = "${TAG}_KEY_QUESTIONS_ANSWERED"
private const val KEY_QUESTIONS_ANSWERED_CORRECTLY = "${TAG}_KEY_QUESTIONS_ANSWERED_CORRECTLY"
private const val KEY_QUESTION_BANK_IS_ACTIVE_STATE = "${TAG}_KEY_QUESTION_BANK_IS_ACTIVE_STATE"
private const val KEY_QUESTION_BANK_IS_CHEATED_STATE = "${TAG}_KEY_QUESTION_BANK_IS_CHEATED_STATE"

class QuizViewModel : ViewModel() {

    private var currentIndex = 0
    private var questionsAnswered: Double = 0.0
    private var questionsAnsweredCorrectly = 0.0

    private val questionBank = listOf(
        Question(R.string.question_australia_text, true),
        Question(R.string.question_oceans_text, true),
        Question(R.string.question_mideast_text, false),
        Question(R.string.question_africa_text, false),
        Question(R.string.question_americas_text, true),
        Question(R.string.question_asia_text, true)
    )

    init {
        Log.d(TAG, "ViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }

    fun getState() = bundleOf(
        KEY_CURRENT_INDEX to currentIndex,
        KEY_QUESTIONS_ANSWERED to questionsAnswered,
        KEY_QUESTIONS_ANSWERED_CORRECTLY to questionsAnsweredCorrectly,
        KEY_QUESTION_BANK_IS_ACTIVE_STATE to questionBank.map { it.isActive }.toBooleanArray(),
        KEY_QUESTION_BANK_IS_CHEATED_STATE to questionBank.map { it.isCheated }.toBooleanArray()
    )

    fun setState(bundle: Bundle?) {
        bundle?.run {
            currentIndex = getInt(KEY_CURRENT_INDEX, 0)
            questionsAnswered = getDouble(KEY_QUESTIONS_ANSWERED, 0.0)
            questionsAnsweredCorrectly = getDouble(KEY_QUESTIONS_ANSWERED_CORRECTLY, 0.0)
            getBooleanArray(KEY_QUESTION_BANK_IS_ACTIVE_STATE)?.forEachIndexed { index, value ->
                questionBank[index].isActive = value
            }
            getBooleanArray(KEY_QUESTION_BANK_IS_CHEATED_STATE)?.forEachIndexed { index, value ->
                questionBank[index].isCheated = value
            }
        }
    }

    val currentQuestionText
        get() = questionBank[currentIndex].textResId

    val currentQuestionAnswer
        get() = questionBank[currentIndex].answer

    val numberOfHints
        get() = Pair(questionBank.count { it.isCheated }, questionBank.size / 2)

    var currentQuestionIsActive
        get() = questionBank[currentIndex].isActive
        set(isActive) {
            questionBank[currentIndex].isActive = isActive
        }

    var currentQuestionIsCheated
        get() = questionBank[currentIndex].isCheated
        set(isCheated) {
            questionBank[currentIndex].isCheated = isCheated
        }

    fun incrementQuestionsAnswered() {
        questionsAnswered++
    }

    fun incrementQuestionsAnsweredCorrectly() {
        questionsAnsweredCorrectly++
    }

    fun toPreviousQuestion() {
        currentIndex = (currentIndex - 1) % questionBank.size
        if (currentIndex < 0) currentIndex = questionBank.size - 1
    }

    fun toNextQuestion() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun isQuizFinished(): Boolean = questionsAnswered == questionBank.size.toDouble()

    fun getScore(): Int = (questionsAnsweredCorrectly / questionsAnswered * 100).toInt()
}
