package ca.gbc.comp3074.milani.labex5

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.core.content.ContextCompat
import ca.gbc.comp3074.milani.labex5.ui.theme.LabEx5Theme

// The activity itself is the LocationListener: Android calls onLocationChanged() on it
class MainActivity : ComponentActivity(), LocationListener {
    private lateinit var locationManager: LocationManager

    // Compose redraws the screen whenever one of these changes
    private var hasPermission by mutableStateOf(false)
    private var latitude by mutableStateOf<Double?>(null)
    private var longitude by mutableStateOf<Double?>(null)

    // Shows the system permission dialog and tells us what the user chose
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasPermission = results[Manifest.permission.ACCESS_FINE_LOCATION] == true
        startLocationUpdates()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        setContent {
            LabEx5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationScreen(
                        hasPermission = hasPermission,
                        latitude = latitude,
                        longitude = longitude,
                        onRequestPermission = {
                            // FINE for the GPS, COARSE because Android 12+ expects both
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // Start listening when the app is on screen...
    override fun onResume() {
        super.onResume()
        // the user may have changed the permission in Settings, so check again
        hasPermission = hasFineLocation()
        startLocationUpdates()
    }

    // ...and stop when it is not, so the GPS is not left running in the background
    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }

    private fun hasFineLocation(): Boolean =
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission") // we check the permission on the first line
    private fun startLocationUpdates() {
        if (!hasFineLocation()) return
        // GPS provider, at most one update per second, no minimum distance
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 1000L, 0f, this
        )
    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
    }

    // Not used in this lab, but Android 10 and older need all three methods implemented
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    @Deprecated("Deprecated in API 29")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}

// Stateless screen: it only shows what it is given, like CustomerScreen in LabEx4
@Composable
fun LocationScreen(
    hasPermission: Boolean,
    latitude: Double?,
    longitude: Double?,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Current Location",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        if (!hasPermission) {
            Text("This app needs your precise location to show your GPS coordinates.")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRequestPermission) {
                Text("Allow location access")
            }
        } else if (latitude == null || longitude == null) {
            Text("Waiting for GPS signal...")
        } else {
            Text("Latitude: ${"%.6f".format(latitude)}")
            Text("Longitude: ${"%.6f".format(longitude)}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    LabEx5Theme {
        LocationScreen(
            hasPermission = true,
            latitude = 43.653226,
            longitude = -79.383184,
            onRequestPermission = {}
        )
    }
}
