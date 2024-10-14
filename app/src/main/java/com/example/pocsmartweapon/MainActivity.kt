package com.example.pocsmartweapon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pocsmartweapon.ui.theme.PocSmartWeaponTheme
import com.impinj.octane.ImpinjReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocSmartWeaponTheme {

                val scope = rememberCoroutineScope()

                fun connect(){
                    scope.launch(Dispatchers.IO) {
                        try {
                            val reader = ImpinjReader()
                            reader.name = "SpeedwayR-12-2E-25"
                            reader.connectTimeout = 60000
                            reader.connect("192.168.1.35")


//                            reader.connect("192.168.1.35")
                        }
                        catch (e : Exception){
                            e.printStackTrace()
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    LaunchedEffect(Unit) {
                        connect()
                    }

                    Button(onClick = { connect() }, modifier = Modifier.padding(16.dp)) {
                        Text("Connect to reader")
                    }
                }
            }
        }
    }
}