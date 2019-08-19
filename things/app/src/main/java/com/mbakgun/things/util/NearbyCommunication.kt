package com.mbakgun.things.util

import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.google.gson.Gson
import com.mbakgun.things.R
import com.mbakgun.things.data.IrDao
import com.mbakgun.things.data.IrData
import com.mbakgun.things.data.NearbyMessage
import com.mbakgun.things.data.NearbyType
import com.mbakgun.things.ui.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import javax.inject.Inject

/**
 * Created by burakakgun on 8.06.2019.
 */
@Suppress("DEPRECATION")
class NearbyCommunication @Inject constructor(private val activity: MainActivity, private val irDao: IrDao) {
    lateinit var mGoogleApiClient: GoogleApiClient
    val mRemotePeerEndpoints = mutableListOf<String>()

    companion object {
        private const val TAG = "NearbyCommunication"
    }

    init {
        mGoogleApiClient = GoogleApiClient.Builder(activity)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(@Nullable bundle: Bundle?) {
                    Log.d(TAG, "onConnected: advertises on the network as the host")
                    startAdvertising()
                }

                override fun onConnectionSuspended(i: Int) {
                    Log.d(TAG, "onConnectionSuspended: $i")
                    mGoogleApiClient.reconnect()
                }
            })
            .addOnConnectionFailedListener { connectionResult -> Log.d(TAG, "onConnectionFailed: $connectionResult") }
            .addApi(Nearby.CONNECTIONS_API)
            .build()
    }

    fun connect() {
        if (mGoogleApiClient.isConnected.not()) mGoogleApiClient.connect()
    }

    fun disconnect() {
        if (mGoogleApiClient.isConnected()) {
            Nearby.Connections.stopAdvertising(mGoogleApiClient)
            if (mRemotePeerEndpoints.isNotEmpty()) {
                Nearby.Connections.sendPayload(
                    mGoogleApiClient,
                    mRemotePeerEndpoints,
                    Payload.fromBytes("Shutting down host".toByteArray())
                )
                Nearby.Connections.stopAllEndpoints(mGoogleApiClient)
                mRemotePeerEndpoints.clear()
            }

            mGoogleApiClient.disconnect()
        }
    }

    fun sendMessage(message: String) {
        Log.d(TAG, "About to send message: $message")
        Nearby.Connections.sendPayload(
            mGoogleApiClient,
            mRemotePeerEndpoints,
            Payload.fromBytes(message.toByteArray(Charset.forName("UTF-8")))
        )
    }

    private fun startAdvertising() = Nearby.Connections
        .startAdvertising(
            mGoogleApiClient, null, activity.getString(R.string.id), object : ConnectionLifecycleCallback() {
                override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                    Log.d(TAG, "onConnectionInitiated. Token: " + connectionInfo.authenticationToken)
                    Nearby.Connections.acceptConnection(mGoogleApiClient, endpointId, object : PayloadCallback() {
                        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) = Unit

                        override fun onPayloadReceived(endpointId: String, payload: Payload) {
                            String(payload.asBytes()!!).apply {
                                Log.d(TAG, "onPayloadReceived: $this")
                                val message = Gson().fromJson<NearbyMessage>(this, NearbyMessage::class.java)
                                when (message.nearbyType) {
                                    NearbyType.UPDATE -> {
                                        val irData = Gson().fromJson<IrData>(message.value, IrData::class.java)
                                        GlobalScope.launch {
                                            irDao.update(irData)
                                            sendMessage(Gson().toJson(NearbyMessage(NearbyType.MESSAGE, "Updated")))
                                        }
                                    }
                                    NearbyType.DELETE -> {
                                        val irData = Gson().fromJson<IrData>(message.value, IrData::class.java)
                                        GlobalScope.launch {
                                            irDao.delete(irData)
                                            sendMessage(Gson().toJson(NearbyMessage(NearbyType.MESSAGE, "Deleted")))
                                        }
                                    }
                                    NearbyType.GET_ALL -> {
                                        GlobalScope.launch {
                                            sendMessage(
                                                Gson().toJson(
                                                    NearbyMessage(
                                                        NearbyType.GET_ALL,
                                                        Gson().toJson(irDao.getAll)
                                                    )
                                                )
                                            )
                                        }
                                    }
                                    NearbyType.MESSAGE -> activity.onNearByDeviceRequestedSerialCommand(message.value)
                                }
                            }
                        }
                    })
                }

                override fun onConnectionResult(endpointId: String, resolution: ConnectionResolution) {
                    Log.d(TAG, "onConnectionResult")
                    if (resolution.status.isSuccess) {
                        if (!mRemotePeerEndpoints.contains(endpointId)) {
                            mRemotePeerEndpoints.add(endpointId)
                        }
                        Log.d(TAG, "Connected! (endpointId=$endpointId)")
                    } else {
                        Log.w(TAG, "Connection to " + endpointId + " failed. Code: " + resolution.status.statusCode)
                    }
                }

                override fun onDisconnected(endpointId: String) {
                    Log.i(TAG, "onDisconnected: $endpointId")
                }
            },
            AdvertisingOptions(Strategy.P2P_STAR)
        )
        .setResultCallback { result ->
            Log.d(TAG, "startAdvertising:onResult:$result")
            if (result.status.isSuccess) {
                Log.d(TAG, "Advertising...")
            }
        }
}
