package com.mbakgun.mobile.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.mbakgun.mobile.data.NearbyMessage
import com.mbakgun.mobile.util.NearbyCommunication
import javax.inject.Inject

/**
 * Created by burakakgun on 8.06.2019.
 */
class MainActivityVM @Inject constructor(private val nearbyCommunication: NearbyCommunication) :
    ViewModel() {

    fun nearByMessageObserver(): MutableLiveData<String> = nearbyCommunication.remoteMessage

    fun nearByConnectivityObserver(): MutableLiveData<Boolean> = nearbyCommunication.mIsConnected

    fun send(message: NearbyMessage) {
        nearbyCommunication.sendMessage(Gson().toJson(message))
    }

    fun connect() = nearbyCommunication.mGoogleApiClient.connect()
}
