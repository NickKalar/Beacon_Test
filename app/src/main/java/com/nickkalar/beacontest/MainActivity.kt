package com.nickkalar.beacontest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.BleSignal
import com.google.android.gms.nearby.messages.Distance
import com.google.android.gms.nearby.messages.Message

class MainActivity : AppCompatActivity() {

    val tag = "MainActivity"
    private val mMessage = Message("Beacon Found".toByteArray())
    var beaconDistance: TextView? = null
    var beaconSignal: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag, "OnCreate called")
        beaconDistance= this.findViewById(R.id.beaconDistanceView)
        beaconSignal= this.findViewById(R.id.beaconSignalView)
    }

    // Object to listen for beacons
    private val mMessageListener = object : MessageListener() {
        // function called when signal from beacon is found
        override fun onFound(message: Message?) {
            message?.let {
                Log.d(tag, "Found Message: " + String(message.content))
                Toast.makeText(
                    this@MainActivity.applicationContext,
                    "Beacon(s) found.",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        // function called when signal to beacon is lost
        override fun onLost(message: Message) {
            Log.d(tag, "Lost Message: " + String(message.content))
            Toast.makeText(
                this@MainActivity.applicationContext,
                "Beacon(s) lost.",
                Toast.LENGTH_LONG
            ).show()
        }

        // function called when the beacon is first connected and then subsequently after the
        // perceived distance to beacon is changed
        override fun onDistanceChanged(p0: Message?, p1: Distance?) {
            super.onDistanceChanged(p0, p1)
            Log.d(tag, "Distance Changed: " + p1.toString())
            beaconDistance?.text = getString(R.string.distance_changed, p1.toString())
        }

        // function called when the beacon is first connected and then subsequently after a message
        // is received with a different RSSI (Relative Signal Strength Indicator)
        override fun onBleSignalChanged(p0: Message?, p1: BleSignal?) {
            super.onBleSignalChanged(p0, p1)
            Log.d(tag, "Signal Changed: " + p1.toString())
            beaconSignal?.text = getString(R.string.signal_changed, p1.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "OnStart called")

        // call to subscribe to BLE messages
        Nearby.getMessagesClient(this).subscribe(mMessageListener)

        Toast.makeText(
            this@MainActivity.applicationContext,
            "Searching for Beacons.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onStop() {
        Log.d(tag, "OnStop called")

        // call to unsubscribe to BLE messages, if this isn't called with onStop, the subscription
        // will continue and can drain the user's battery life
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener)

        super.onStop()
    }
}
