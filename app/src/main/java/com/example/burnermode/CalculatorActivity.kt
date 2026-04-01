package com.example.burnermode

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class CalculatorActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private var currentInput = ""
    private var lastValue = 0.0
    private var pendingOp = ""
    private val df = DecimalFormat("#.#######")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        display = findViewById(R.id.tvDisplay)

        val listener = View.OnClickListener { v ->
            val b = v as Button
            val text = b.text.toString()
            
            when {
                text == "C" -> {
                    currentInput = ""
                    lastValue = 0.0
                    pendingOp = ""
                    display.text = "0"
                }
                text == "DEL" -> {
                    if (currentInput.isNotEmpty()) {
                        currentInput = currentInput.dropLast(1)
                        display.text = if (currentInput.isEmpty()) "0" else currentInput
                    }
                }
                text == "=" -> {
                    if (currentInput == "1234") { // SECRET CODE
                        val intent = Intent(this, BurnerActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else if (pendingOp.isNotEmpty() && currentInput.isNotEmpty()) {
                        calculate()
                        pendingOp = ""
                    }
                }
                text in listOf("+", "-", "*", "/") -> {
                    if (currentInput.isNotEmpty()) {
                        if (pendingOp.isNotEmpty()) calculate() else lastValue = currentInput.toDouble()
                        pendingOp = text
                        currentInput = ""
                    }
                }
                else -> { // Numbers and decimal
                    if (currentInput.length < 12) {
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

    private fun calculate() {
        val secondValue = currentInput.toDouble()
        when (pendingOp) {
            "+" -> lastValue += secondValue
            "-" -> lastValue -= secondValue
            "*" -> lastValue *= secondValue
            "/" -> if (secondValue != 0.0) lastValue /= secondValue
        }
        currentInput = ""
        display.text = df.format(lastValue)
    }
}
