package com.mbakgun.things.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import android.util.Log.d
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import com.mbakgun.things.R
import com.mbakgun.things.data.IrDao
import com.mbakgun.things.data.IrData
import com.mbakgun.things.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import javax.inject.Inject

/**
 * Created by burakakgun on 8.06.2019.
 */
class SerialCommunication @Inject constructor(private val activity: MainActivity, private val irDao: IrDao) {

    private var connection: UsbDeviceConnection? = null
    private var serialDevice: UsbSerialDevice? = null
    private var buffer = ""

    private val usbManager by lazy {
        activity.getSystemService(UsbManager::class.java)
    }

    fun initSerialConnectionOverUSB() {
        val filter = IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        activity.registerReceiver(usbDetachedReceiver, filter)
    }

    private val usbDetachedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (device != null && device.vendorId == USB_VENDOR_ID && device.productId == USB_PRODUCT_ID) {
                    Log.i(TAG, "USB device detached")
                    stopUsbConnection()
                }
            }
        }
    }

    fun startUsbConnection() {
        val connectedDevices = usbManager.deviceList
        if (connectedDevices.isNotEmpty()) {
            for (device in connectedDevices.values) {
                if (device.vendorId == USB_VENDOR_ID && device.productId == USB_PRODUCT_ID) {
                    Log.i(TAG, "Device found: " + device.deviceName)
                    startSerialConnection(device)
                    return
                }
            }
        }
        Log.w(TAG, "Could not start USB connection - No devices found")
    }

    private fun startSerialConnection(device: UsbDevice) {
        Log.i(TAG, "Ready to open USB device connection")
        connection = usbManager.openDevice(device)
        serialDevice = UsbSerialDevice.createUsbSerialDevice(device, connection)
        if (serialDevice!!.open()) {
            with(serialDevice!!) {
                setBaudRate(BAUD_RATE)
                setDataBits(UsbSerialInterface.DATA_BITS_8)
                setStopBits(UsbSerialInterface.STOP_BITS_1)
                setParity(UsbSerialInterface.PARITY_NONE)
                setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
                read(callback)
            }
            Log.i(TAG, "Serial connection opened")
        } else {
            Log.w(TAG, "Cannot open serial connection")
        }
    }

    fun sendSerialData(value: String) {
        serialDevice?.write(value.toByteArray())
    }

    fun sendVoiceData(speech: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val value = irDao.getByName(speech)
            value?.let {
                d("SerialCommunication", "speech sending:$speech")
                serialDevice?.write("send:${value.hexCode}".toByteArray())
            }
        }
    }

    fun stopUsbConnection() {
        activity.unregisterReceiver(usbDetachedReceiver)
        try {
            serialDevice?.close()
            connection?.close()
        } finally {
            serialDevice = null
            connection = null
        }
    }

    private val callback = UsbSerialInterface.UsbReadCallback { data ->
        try {
            val dataUtf8 = String(data, Charset.forName("UTF-8"))
            buffer += dataUtf8
            var index = 0
            while ({
                    index = buffer.indexOf('\n')
                    index
                }() != -1) {
                val dataStr = buffer.substring(0, index + 1).trim { it <= ' ' }
                buffer = if (buffer.length == index) "" else buffer.substring(index + 1)

                Log.i(TAG, "Serial data received: $data")
                if (dataStr.startsWith("saved")) {
                    val irData =
                        IrData(
                            name = dataStr.substring(SUBSTRING_STARTS, dataStr.indexOf("-")),
                            hexCode = dataStr.substring(dataStr.indexOf("-") + 1)
                        )
                    if (irData.hexCode != "FFFFFFFF") {
                        // end of button push
                        GlobalScope.launch {
                            irDao.insert(irData)
                            activity.onSerialDataReceived(activity.getString(R.string.saved))
                        }
                    } else {
                        activity.onSerialDataReceived(activity.getString(R.string.try_again))
                    }
                } else {
                    activity.onSerialDataReceived(dataStr)
                }
            }
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "Error receiving USB data", e)
        }
    }

    companion object {
        const val TAG = "SerialCommunication"
        const val USB_VENDOR_ID = 6790
        const val USB_PRODUCT_ID = 29987
        const val BAUD_RATE = 9600
        const val SUBSTRING_STARTS = 6 // 'read:' and 'send:'
    }
}
