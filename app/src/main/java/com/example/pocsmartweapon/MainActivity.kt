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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pocsmartweapon.ui.theme.PocSmartWeaponTheme
import com.impinj.octane.AntennaConfigGroup
import com.impinj.octane.BitPointers
import com.impinj.octane.ImpinjReader
import com.impinj.octane.MemoryBank
import com.impinj.octane.ReaderMode
import com.impinj.octane.ReportConfig
import com.impinj.octane.ReportMode
import com.impinj.octane.TagFilter
import com.impinj.octane.TagFilterMode
import com.impinj.octane.TagFilterOp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocSmartWeaponTheme {

                val scope = rememberCoroutineScope()

                val reader by remember { mutableStateOf(ImpinjReader()) }


                LaunchedEffect(Unit) {
                    scope.launch(Dispatchers.IO) {
                        reader.name = "SpeedwayR-12-2E-25"
                        reader.connect("192.168.1.35")
                    }
                }

                fun connect(){
                    try {
                        val settings = reader.queryDefaultSettings()

                        val report: ReportConfig = settings.getReport()
                        report.includeAntennaPortNumber = true
                        report.includePeakRssi = true
                        report.includePhaseAngle = true
                        report.includeFirstSeenTime = true
                        report.includeChannel = true
                        report.mode = ReportMode.Individual

                        settings.setReaderMode(ReaderMode.AutoSetDenseReader)


                        val t1: TagFilter = settings.getFilters().getTagFilter1()
                        t1.bitPointer = BitPointers.Epc.toInt()
                        t1.memoryBank = MemoryBank.Epc
                        t1.filterOp = TagFilterOp.Match

                        settings.getFilters().setMode(TagFilterMode.OnlyFilter1)


                        // set some special settings for antenna 1
                        val antennas: AntennaConfigGroup = settings.getAntennas()
                        antennas.disableAll()
                        antennas.enableById(shortArrayOf(1))
                        antennas.getAntenna(1.toShort()).isMaxRxSensitivity = false
                        antennas.getAntenna(1.toShort()).isMaxTxPower = false
                        antennas.getAntenna(1.toShort()).txPowerinDbm = 25.0
                        antennas.getAntenna(1.toShort()).rxSensitivityinDbm = -70.0

                        reader.applySettings(settings)

                        reader.setTagOpCompleteListener { impinjReader, tagOpReport ->
                            Log.d("Tag Op Complete","Tag operation complete from ${impinjReader.name}, ${impinjReader.address}, $tagOpReport")
                        }

                        reader.setKeepaliveListener { impinjReader, keepaliveEvent ->
                            Log.d(" Keep Alive","Keepalive received from ${impinjReader.name}, ${impinjReader.address}, $keepaliveEvent")
                        }

                        reader.setTagReportListener { impinjReader, tagReport ->
                            for (tag in tagReport.tags) {
                                Log.d("Tag Report", "EPC: ${tag.epc} ID : ${tag.tid}")
                            }
                        }
                        reader.start()
                    }
                    catch (e : Exception){
                        // Show toast
                        Toast.makeText(this@MainActivity, "Error connecting to reader", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
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