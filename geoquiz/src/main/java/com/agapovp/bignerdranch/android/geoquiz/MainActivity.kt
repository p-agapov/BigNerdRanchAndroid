package com.agapovp.bignerdranch.android.geoquiz

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var buttonTrue: Button
    private lateinit var buttonFalse: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonTrue = findViewById<Button?>(R.id.button_true).apply {
            setOnClickListener {
                Toast.makeText(context, R.string.toast_true_text, Toast.LENGTH_LONG).apply {
                    setGravity(Gravity.TOP, 0, 250)
                }.show()
            }
        }
        buttonFalse = findViewById<Button?>(R.id.button_false).apply {
            setOnClickListener {
                Toast.makeText(context, R.string.toast_false_text, Toast.LENGTH_SHORT).apply {
                    setGravity(Gravity.TOP, 0, 250)
                }.show()
            }
        }
    }
}
