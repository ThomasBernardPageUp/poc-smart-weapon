package com.example.pocsmartweapon

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pocsmartweapon.ui.theme.PocSmartWeaponTheme
import com.impinj.octane.ImpinjReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocSmartWeaponTheme {

                val scope = rememberCoroutineScope()

                val reader by remember { mutableStateOf(ImpinjReader()) }

                fun connect(){
                    scope.launch(Dispatchers.IO) {
                        try {
                            reader.name = "SpeedwayR-12-2E-25"
                            reader.connect("192.168.1.35")


                            reader.setTagOpCompleteListener { impinjReader, tagOpReport ->
                                Log.d("Tag Op Complete","Tag operation complete from ${impinjReader.name}, ${impinjReader.address}, $tagOpReport")
                            }

                            reader.setKeepaliveListener { impinjReader, keepaliveEvent ->
                                Log.d(" Keep Alive","Keepalive received from ${impinjReader.name}, ${impinjReader.address}, $keepaliveEvent")
                            }

                            reader.setTagReportListener { impinjReader, tagReport ->
                                for (tag in tagReport.tags) {
                                    Log.d("Tag Report", "EPC: ${tag.epc}")
                                }
                            }

                            reader.start()
                        }
                        catch (e : Exception){
                            // Show toast
                            withContext(Dispatchers.Main){
                                Toast.makeText(this@MainActivity, "Error connecting to reader", Toast.LENGTH_SHORT).show()
                            }
                            e.printStackTrace()
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Row {
                        Button(onClick = { connect() }, modifier = Modifier.padding(16.dp)) {
                            Text("Connect to reader")
                        }

                        Button(onClick = { reader.disconnect() }, modifier = Modifier.padding(16.dp)) {
                            Text("Logout from reader")
                        }
                    }
                }
            }
        }
    }
}