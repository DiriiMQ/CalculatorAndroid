package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                        Calculator(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

object CalHandler {
    private var currentInput: String = "0"
    private var currentOperator: String = ""
    private var previousValue: Double = 0.0
    private var inOperation: Boolean = false

    fun handleButtonClick(buttonText: String) {
        when (buttonText) {
            in "0".."9" -> {
                if (currentInput == "0") currentInput = ""
                if (inOperation) {
                    currentInput = ""
                    inOperation = false
                }

                currentInput += buttonText
            }
            "." -> {
                if (!currentInput.contains(".")) {
                    currentInput += "."
                }
            }
            in "+", "-", "×", "÷" -> {
                if (currentOperator.isNotEmpty() && currentInput.isNotEmpty()) {
                    handleButtonClick("=")
                }

                previousValue = currentInput.toDoubleOrNull() ?: 0.0
                currentOperator = buttonText
                currentInput = previousValue.toString()
                inOperation = true
            }
            "=" -> {
                val currentValue = currentInput.toDoubleOrNull() ?: 0.0
                currentInput = when (currentOperator) {
                    "+" -> (previousValue + currentValue).toString()
                    "-" -> (previousValue - currentValue).toString()
                    "×" -> (previousValue * currentValue).toString()
                    "÷" -> (previousValue / currentValue).toString()
                    else -> currentInput
                }
                inOperation = true
            }
            "C" -> {
                currentInput = "0"
                currentOperator = ""
                previousValue = 0.0
            }
            "±" -> {
                currentInput = (currentInput.toDouble() * -1).toString()
            }
            "%" -> {
                currentInput = (currentInput.toDouble() / 100).toString()
            }
        }
    }

    fun getCurrentInput(limitCharacter: Int = 10): String {
        // remove .0 if it's a whole number
        // return with limit of 10 characters
        // if ans is greater than 10 characters, return double in scientific notation
        if (currentInput.toDouble() < 0) {
            limitCharacter.inc()
        }
        var doubleVal = currentInput.toDouble().toString().take(limitCharacter)
        if (doubleVal.endsWith(".0")) {
            doubleVal = doubleVal.substring(0, doubleVal.length - 2)
        }
        return doubleVal
    }
}

@Composable
fun Calculator(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CalDisplay(modifier = Modifier.weight(0.8f))
        CalKeyboard(modifier = Modifier.weight(2f))
    }
}

@Composable
fun CalDisplay(modifier: Modifier = Modifier) {
    var current by remember { mutableStateOf("0") }

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomEnd
    ) {
        LaunchedEffect(key1 = current) {
            while (true) {
                delay(100)
                current = CalHandler.getCurrentInput()
            }
        }
        Text(
            text = current,
            fontSize = 60.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun CalKeyboard(modifier: Modifier = Modifier) {
    var buttons = listOf(
        listOf("C", "±", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "=")
    )

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { buttonText ->

                    Button(
                        onClick = { CalHandler.handleButtonClick(buttonText) }
                    ) {
                        Text(
                            text = buttonText,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculatorTheme {
        Calculator()
    }
}