package ca.gbc.comp3074.milani_arman.labex6

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.milani_arman.labex6.ui.theme.LabEx6Theme

// The activity itself is the SensorEventListener: Android calls onSensorChanged() on it
class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager

    // getDefaultSensor() returns null when the phone has no such sensor, so these are nullable
    private var temperatureSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null

    // Compose redraws the screen whenever one of these changes
    private var temperature by mutableStateOf<Float?>(null)
    private var gyroX by mutableStateOf(0f)
    private var gyroY by mutableStateOf(0f)
    private var gyroZ by mutableStateOf(0f)

    private var temperatureAvailable by mutableStateOf(false)
    private var gyroscopeAvailable by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // null means the hardware is missing: the screen says "not available" instead of crashing
        temperatureAvailable = temperatureSensor != null
        gyroscopeAvailable = gyroscopeSensor != null

        setContent {
            LabEx6Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SensorScreen(
                        temperature = temperature,
                        temperatureAvailable = temperatureAvailable,
                        gyroX = gyroX,
                        gyroY = gyroY,
                        gyroZ = gyroZ,
                        gyroscopeAvailable = gyroscopeAvailable,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // Start listening when the app is on screen...
    override fun onResume() {
        super.onResume()
        // ?.let only runs if the sensor exists, so a missing sensor is skipped safely
        temperatureSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscopeSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // ...and stop when it is not, so the sensors do not drain the battery in the background
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // Called every time a registered sensor has a new reading
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        // Both sensors report to this one method, so check which one it was
        when (event.sensor.type) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                temperature = event.values[0] // degrees Celsius
            }

            Sensor.TYPE_GYROSCOPE -> {
                gyroX = event.values[0] // rad/s around the X axis
                gyroY = event.values[1] // rad/s around the Y axis
                gyroZ = event.values[2] // rad/s around the Z axis
            }
        }
    }

    // Not used in this lab, but SensorEventListener forces us to implement it
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

// Stateless screen: it only shows what it is given, like LocationScreen in LabEx5
@Composable
fun SensorScreen(
    temperature: Float?,
    temperatureAvailable: Boolean,
    gyroX: Float,
    gyroY: Float,
    gyroZ: Float,
    gyroscopeAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Android Sensor Monitor",
            style = MaterialTheme.typography.headlineMedium
        )

        TemperatureCard(
            temperature = temperature,
            available = temperatureAvailable
        )

        GyroscopeCard(
            x = gyroX,
            y = gyroY,
            z = gyroZ,
            available = gyroscopeAvailable
        )
    }
}

// Three possible states: sensor missing, sensor present but no reading yet, reading received
@Composable
fun TemperatureCard(temperature: Float?, available: Boolean) {
    SensorValueCard(title = "Ambient Temperature") {
        when {
            !available -> Text("Temperature sensor is not available.")
            temperature == null -> Text("Waiting for sensor reading...")
            else -> Text(
                text = "%.1f °C".format(temperature),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun GyroscopeCard(x: Float, y: Float, z: Float, available: Boolean) {
    SensorValueCard(title = "Gyroscope") {
        if (!available) {
            Text("Gyroscope is not available.")
        } else {
            Text("X: %.3f rad/s".format(x))
            Text("Y: %.3f rad/s".format(y))
            Text("Z: %.3f rad/s".format(z))
        }
    }
}

// Independent practice: one reusable card, so every sensor gets the same frame.
// The title is a parameter and the card body is a lambda, the same way Column { ... } works
@Composable
fun SensorValueCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SensorScreenPreview() {
    LabEx6Theme {
        SensorScreen(
            temperature = 21.5f,
            temperatureAvailable = true,
            gyroX = 0.012f,
            gyroY = -0.340f,
            gyroZ = 0.005f,
            gyroscopeAvailable = true
        )
    }
}

// What the phone shows when it has neither sensor
@Preview(showBackground = true)
@Composable
fun SensorScreenUnavailablePreview() {
    LabEx6Theme {
        SensorScreen(
            temperature = null,
            temperatureAvailable = false,
            gyroX = 0f,
            gyroY = 0f,
            gyroZ = 0f,
            gyroscopeAvailable = false
        )
    }
}
