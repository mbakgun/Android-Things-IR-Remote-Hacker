package com.mbakgun.mobile.util

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.mbakgun.mobile.App
import com.mbakgun.mobile.R
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by burakakgun on 8.06.2019.
 */
@Suppress("DEPRECATION")
@Singleton
class NearbyCommunication @Inject constructor(private val app: App) {

    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var mRemoteHostEndpoint: String
    val mIsConnected: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }
    val remoteMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    companion object {
        private const val TAG = "NearbyCommunication"
    }

    init {
        mGoogleApiClient = GoogleApiClient.Builder(app)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(@Nullable bundle: Bundle?) {
                    Log.d(
                        TAG,
                        "onConnected: start discovering hosts to send connection requests"
                    )
                    startDiscovery()
                }

                override fun onConnectionSuspended(i: Int) {
                    Log.d(TAG, "onConnectionSuspended: $i")
                    mGoogleApiClient.reconnect()
                }
            })
            .addOnConnectionFailedListener { connectionResult ->
                Log.d(
                    TAG,
                    "onConnectionFailed: " + connectionResult.errorCode
                )
            }
            .addApi(Nearby.CONNECTIONS_API)
            .build()
    }

    fun sendMessage(message: String) {
        Log.d(TAG, "About to send message: $message")
        Nearby.Connections.sendPayload(
            mGoogleApiClient,
            mRemoteHostEndpoint,
            Payload.fromBytes(message.toByteArray(Charset.forName("UTF-8")))
        )
    }

    private fun startDiscovery() = Nearby.Connections.startDiscovery(
        mGoogleApiClient, app.getString(R.string.id), object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                Log.d(TAG, "onEndpointFound:" + endpointId + ":" + info.endpointName)

                Nearby.Connections
                    .requestConnection(mGoogleApiClient, null, endpointId, object : ConnectionLifecycleCallback() {
                        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                            Log.d(TAG, "onConnectionInitiated. Token: " + connectionInfo.authenticationToken)
                            Nearby.Connections.acceptConnection(
                                mGoogleApiClient,
                                endpointId,
                                object : PayloadCallback() {
                                    override fun onPayloadReceived(endpointId: String, payload: Payload) {
                                        String(payload.asBytes()!!).apply {
                                            Log.d(TAG, "onPayloadReceived: $this")
                                            remoteMessage.postValue(this)
                                        }
                                    }

                                    override fun onPayloadTransferUpdate(
                                        endpointId: String,
                                        update: PayloadTransferUpdate
                                    ) = Unit
                                })
                        }

                        override fun onConnectionResult(endpointId: String, resolution: ConnectionResolution) {
                            Log.d(TAG, "onConnectionResult:" + endpointId + ":" + resolution.status)
                            if (resolution.status.isSuccess) {
                                Log.d(TAG, "Connected successfully")
                                Nearby.Connections.stopDiscovery(mGoogleApiClient)
                                mRemoteHostEndpoint = endpointId
                                mIsConnected.postValue(true)
                                Toast.makeText(app, "Connected with Android Things", Toast.LENGTH_SHORT).show()
                            } else {
                                if (resolution.status.statusCode == ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED) {
                                    Log.d(TAG, "The connection was rejected by one or both sides")
                                } else {
                                    Log.d(
                                        TAG,
                                        "Connection to " + endpointId + " failed. Code: " + resolution.status.statusCode
                                    )
                                }
                                mIsConnected.postValue(false)
                            }
                        }

                        override fun onDisconnected(endpointId: String) {
                            Toast.makeText(app, "Disconnected", Toast.LENGTH_SHORT).show()
                            mIsConnected.postValue(false)
                            Log.d(TAG, "onDisconnected: $endpointId")
                        }
                    })
            }

            override fun onEndpointLost(endpointId: String) {
                Log.d(TAG, "onEndpointLost:$endpointId")
            }
        },
        DiscoveryOptions(Strategy.P2P_STAR)
    ).setResultCallback { status ->
        if (status.isSuccess) {
            Log.d(TAG, "Discovering...")
        } else {
            Log.d(
                TAG,
                "Discovering failed: " + status.statusMessage + "(" + status.statusCode + ")"
            )
        }
    }
}
