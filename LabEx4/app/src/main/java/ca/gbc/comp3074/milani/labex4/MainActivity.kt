package ca.gbc.comp3074.milani.labex4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.gbc.comp3074.milani.labex4.data.AppDatabase
import ca.gbc.comp3074.milani.labex4.ui.CustomerApp
import ca.gbc.comp3074.milani.labex4.ui.CustomerViewModel
import ca.gbc.comp3074.milani.labex4.ui.CustomerViewModelFactory
import ca.gbc.comp3074.milani.labex4.ui.theme.LabEx4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)

        setContent {
            LabEx4Theme {
                val viewModel: CustomerViewModel = viewModel(
                    factory = CustomerViewModelFactory(
                        database.customerDao()
                    )
                )
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CustomerApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
