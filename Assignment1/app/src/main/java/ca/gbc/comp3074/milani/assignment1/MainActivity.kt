package ca.gbc.comp3074.milani.assignment1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.milani.assignment1.ui.theme.Assignment1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PayScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PayScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // what the user types (text fields always hold Strings)
    var hours by remember { mutableStateOf("") }
    var hourlyRate by remember { mutableStateOf("") }
    var taxPercent by remember { mutableStateOf("") }

    // what we show under the Calculate button
    var output by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = hours,
            onValueChange = { hours = it },
            label = { Text("Hours worked") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = hourlyRate,
            onValueChange = { hourlyRate = it },
            label = { Text("Hourly rate") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = taxPercent,
            onValueChange = { taxPercent = it },
            label = { Text("Tax rate (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val h = hours.toDoubleOrNull()
                val r = hourlyRate.toDoubleOrNull()
                val t = taxPercent.toDoubleOrNull()

                if (h == null || r == null || t == null) {
                    output = "Please enter a number in every field."
                } else {
                    val pay: Double
                    val overtimePay: Double
                    if (h <= 40) {
                        pay = h * r
                        overtimePay = 0.0
                    } else {
                        pay = 40 * r
                        overtimePay = (h - 40) * r * 1.5
                    }
                    val totalPay = pay + overtimePay

                    val taxRate = t / 100
                    val tax = pay * taxRate

                    output = "Pay: \$%.2f\nOvertime pay: \$%.2f\nTotal pay: \$%.2f\nTax: \$%.2f"
                        .format(pay, overtimePay, totalPay, tax)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate")
        }

        Text(output)

        Button(
            onClick = { context.startActivity(Intent(context, AboutActivity::class.java)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("About")
        }
    }
}
