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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pocsmartweapon.ui.theme.PocSmartWeaponTheme
import com.impinj.octane.ImpinjReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocSmartWeaponTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    LaunchedEffect(Unit) {
                        try {
                            val reader = ImpinjReader()

                            reader.connect("192.168.1.35")
                        }
                        catch (e : Exception){
                            e.printStackTrace()
                        }
                    }

                    Button(onClick = {
                        try {
                            val reader = ImpinjReader()
                            reader.disconnect()
                            reader.connect("192.168.1.35")


//                            reader.connect("192.168.1.35")
                        }
                        catch (e : Exception){
                            e.printStackTrace()
                        }
                    }) {
                        Text("Connect to reader")
                    }
                }
            }
        }
    }
}