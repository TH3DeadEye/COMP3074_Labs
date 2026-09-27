package ca.gbc.comp3074.milani.labex2

import android.content.Intent
import androidx.compose.ui.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ca.gbc.comp3074.milani.labex2.ui.theme.LabEx2Theme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabEx2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ActionButtons(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButtons(modifier: Modifier = Modifier,
                  url: Uri = "https://georgebrown.ca".toUri(),
                  phone: Uri = "tel:4164155000".toUri(),
                  location: Uri = ("geo:0,0?q=" + Uri.encode("160 Kendal Ave, Toronto")).toUri()){

    val cnt = remember { mutableIntStateOf(0) }
    val step = remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxWidth().padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Application Logo",
                modifier = Modifier.width(200.dp).height(100.dp)

            )
            Text("${cnt.intValue}")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),

        ){
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, url)

                    cnt.intValue += step.intValue

                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "icon Language"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text("Browser")
            }
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL,
                        phone
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.purple_200)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "icon call"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text("Call")

            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),

            ){
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW,
                        location
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(1f, 0f, 1f, 1f)
                )

            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "icon Map"

                )

                Spacer(modifier = Modifier.width(5.dp))
                Text("Map")
            }
            Button(
                onClick = {
                    val intent = Intent(
                        context,
                        AboutActivity::class.java
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "icon Info"

                )
                Spacer(modifier = Modifier.width(5.dp))
                Text("About")
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LabEx2Theme {
        ActionButtons()
    }
}