package com.mbakgun.mobile.util

import android.content.Context
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.mbakgun.mobile.R
import com.mbakgun.mobile.data.IrData
import com.mbakgun.mobile.data.NearbyMessage
import com.mbakgun.mobile.data.NearbyType
import com.mbakgun.mobile.ui.MainActivityVM

/**
 * Created by burakakgun on 9.06.2019.
 */

fun showAlertWithTextInputLayout(context: Context, vm: MainActivityVM, irData: IrData? = null) {
    val textInputLayout = TextInputLayout(context)
    textInputLayout.setPadding(
        context.resources.getDimensionPixelOffset(R.dimen.dp_19),
        0,
        context.resources.getDimensionPixelOffset(R.dimen.dp_19),
        0
    )
    val input = EditText(context)
    irData?.let { it ->
        input.setText(it.name)
    }
    textInputLayout.hint = "Remote Device Name"
    textInputLayout.addView(input)
    val alert = AlertDialog.Builder(context)
        .setTitle("Capture Infrared")
        .setView(textInputLayout)
        .setMessage("Please push any IR signal then enter your device name")
        .setPositiveButton("Send") { _, _ ->
            val text = input.text
            if (TextUtils.isEmpty(text).not()) {
                irData?.let {
                    vm.send(NearbyMessage(NearbyType.UPDATE, Gson().toJson(it.copy(name = text.toString()))))
                } ?: run {
                    vm.send(NearbyMessage(NearbyType.MESSAGE, "read:$text"))
                }
            } else {
                Toast.makeText(context, "Device Name is required", Toast.LENGTH_SHORT).show()
            }
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }.create()
    alert.show()
}
