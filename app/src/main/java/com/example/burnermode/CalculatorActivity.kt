package com.example.burnermode

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalculatorActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private var currentInput = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        display = findViewById(R.id.tvDisplay)

        val listener = View.OnClickListener { v ->
            val b = v as Button
            val text = b.text.toString()
            
            when (text) {
                "C" -> {
                    currentInput = ""
                    display.text = "0"
                }
                "DEL" -> {
                    if (currentInput.isNotEmpty()) {
                        currentInput = currentInput.dropLast(1)
                        display.text = if (currentInput.isEmpty()) "0" else currentInput
                    }
                }
                "=" -> {
                    // Hidden "Flip Back" logic: if input is 1234 (or any code you choose), return to Burner login
                    if (currentInput == "1234") {
                        val intent = Intent(this, BurnerActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        currentInput = ""
                        display.text = "Error" // Act like a calculation error or just reset
                    }
                }
                else -> {
                    if (currentInput.length < 10) {
                        currentInput += text
                        display.text = currentInput
                    }
                }
            }
        }

        val grid = findViewById<GridLayout>(R.id.calculatorGrid)
        for (i in 0 until grid.childCount) {
            val child = grid.getChildAt(i)
            if (child is Button) {
                child.setOnClickListener(listener)
            }
        }
    }
}
