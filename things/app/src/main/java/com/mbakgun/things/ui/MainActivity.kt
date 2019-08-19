package com.mbakgun.things.ui

import android.os.Bundle
import com.google.gson.Gson
import com.mbakgun.things.data.NearbyMessage
import com.mbakgun.things.data.NearbyType
import com.mbakgun.things.util.NearbyCommunication
import com.mbakgun.things.util.PocketSphinx
import com.mbakgun.things.util.SerialCommunication
import dagger.android.DaggerActivity
import javax.inject.Inject

class MainActivity : DaggerActivity() {
    @Inject
    lateinit var serialCommunication: SerialCommunication

    @Inject
    lateinit var nearbyCommunication: NearbyCommunication

    @Inject
    lateinit var speechRecognizer: PocketSphinx

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serialCommunication.initSerialConnectionOverUSB()
        speechRecognizer.runRecognizerSetup()
    }

    fun onSerialDataReceived(data: String) =
        nearbyCommunication.sendMessage(
            Gson().toJson(NearbyMessage(NearbyType.MESSAGE, data))
        )

    fun onNearByDeviceRequestedSerialCommand(data: String) = serialCommunication.sendSerialData(data)

    fun onTextRecognized(recognizedText: String) = serialCommunication.sendVoiceData(recognizedText)

    override fun onResume() {
        super.onResume()
        nearbyCommunication.connect()
        serialCommunication.startUsbConnection()
    }

    override fun onStop() {
        super.onStop()
        serialCommunication.stopUsbConnection()
        nearbyCommunication.disconnect()
    }
}
