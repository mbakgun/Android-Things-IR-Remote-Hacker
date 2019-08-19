package com.mbakgun.mobile.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mbakgun.mobile.R
import com.mbakgun.mobile.data.IrData
import com.mbakgun.mobile.data.NearbyMessage
import com.mbakgun.mobile.data.NearbyType.GET_ALL
import com.mbakgun.mobile.data.NearbyType.MESSAGE
import com.mbakgun.mobile.di.ViewModelInjectionField
import com.mbakgun.mobile.di.qualifiers.ViewModelInjection
import com.mbakgun.mobile.util.MarginItemDecoration
import com.mbakgun.mobile.util.showAlertWithTextInputLayout
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * Created by burakakgun on 8.06.2019.
 */
class MainActivity : DaggerAppCompatActivity() {

    @Inject
    @ViewModelInjection
    lateinit var vm: ViewModelInjectionField<MainActivityVM>
    private val mainActivityVM: MainActivityVM = vm.get()

    @Inject
    lateinit var adapter: IrDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivityVM.nearByMessageObserver().observe(this, Observer { message ->
            val data = Gson().fromJson(message, NearbyMessage::class.java)
            if (data.nearbyType == GET_ALL) {
                progressBar.visibility = View.GONE
                adapter.updateList(
                    Gson().fromJson(
                        data.value,
                        object : TypeToken<List<IrData>>() {}.type
                    )
                )
            } else if (data.nearbyType == MESSAGE) {
                Snackbar.make(root, data.value, Snackbar.LENGTH_LONG).show()
                // request new list after all events
                mainActivityVM.send(NearbyMessage(GET_ALL))
            }
        })
        mainActivityVM.nearByConnectivityObserver().observe(this, Observer { value -> applyFloatActionButton(value) })
        setRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        if (checkPermission()) vm.get().connect()
    }

    private fun setRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(MarginItemDecoration(resources.getDimension(R.dimen.dp_8).toInt()))
    }

    private fun applyFloatActionButton(isConnected: Boolean) {
        Log.d("MainActivity", "status : $isConnected")
        if (checkPermission()) {
            if (isConnected) {
                floatingActionButton.clearAnimation()
                floatingActionButton.setImageResource(R.drawable.ic_add)
                floatingActionButton.setOnClickListener {
                    showAlertWithTextInputLayout(this, vm.get())
                }
                vm.get().send(NearbyMessage(GET_ALL))
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.GONE
                floatingActionButton.setImageResource(R.drawable.ic_sync)
                val rotate = RotateAnimation(
                    float_max, 0f,
                    Animation.RELATIVE_TO_SELF, animation_pivotXValue,
                    Animation.RELATIVE_TO_SELF, animation_pivotXValue
                )
                rotate.duration = animation_duration
                rotate.repeatCount = Animation.INFINITE
                floatingActionButton.startAnimation(rotate)
                vm.get().connect()
                floatingActionButton.setOnClickListener {
                    Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_location_disabled)
            floatingActionButton.setOnClickListener {
                checkPermission()
            }
        }
    }

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), REQ_CODE
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQ_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            applyFloatActionButton(false)
        }
    }

    companion object {
        const val REQ_CODE = 1453
        const val float_max = 360f
        const val animation_duration: Long = 1250
        const val animation_pivotXValue = 0.5f
    }
}
