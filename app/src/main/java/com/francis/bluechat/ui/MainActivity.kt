package com.francis.bluechat.ui

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.francis.bluechat.R
import com.francis.bluechat.application.MainApplication
import com.francis.bluechat.data.Device
import com.francis.bluechat.databinding.ActivityMainBinding
import com.francis.bluechat.ui.adapter.DeviceAdapter
import com.francis.bluechat.ui.dagger.MainComponent
import com.francis.bluechat.utils.SnackBarUtil
import com.francis.bluechat.utils.missingSystemFeature
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity(), Provider<MainComponent> {

    private lateinit var binding: ActivityMainBinding

    private lateinit var bluetoothAdapter: BluetoothAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(
            MainActivityViewModel::class.java
        )
    }

    private var adapter = DeviceAdapter(object : DeviceAdapter.DeviceClickListener {
        override fun onclick(device: Device) {
            // TODO("Not yet implemented")
        }
    })

    private val bluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                queryPairedDevices()
            } else {
                SnackBarUtil.instance?.showSnackBar(this, "Error turning on bluetooth")
            }
        }

    private val bluetoothDiscoverabilityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                DURATION -> {
                    TODO("")
                }
                Activity.RESULT_CANCELED -> {
                    TODO("")
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        get().inject(this)

        checkBluetoothFeature()
        checkToEnable()

        binding.recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.visibility -> {
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).let { intent ->
                    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DURATION)
                    bluetoothDiscoverabilityLauncher.launch(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun checkBluetoothFeature() {
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH) }?.also {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.bluetooth_unavailable_dialog_title))
                setMessage(getString(R.string.bluetooth_unavailable_dialog_message))
                setPositiveButton(getString(R.string.text_OK)) { d, _ ->
                    d.dismiss()
                }
                setOnDismissListener { _ ->
                    finish()
                }
                create()
            }.show()
        }
    }

    private fun checkToEnable() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).let { intent ->
                bluetoothLauncher.launch(intent)
            }
        } else if (bluetoothAdapter.isEnabled) {
            queryPairedDevices()
        }
    }

    private fun queryPairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        val devices = pairedDevices
            ?.filter { isPhone(it) }
            ?.map { Device(it.name, it.address) }
        adapter.submitList(devices)
    }

    private fun isConnected(device: BluetoothDevice): Boolean {
        return try {
            val m: Method = device.javaClass.getMethod("isConnected")
            m.invoke(device) as Boolean
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    private fun isPhone(device: BluetoothDevice): Boolean =
        device.bluetoothClass.deviceClass == BluetoothClass.Device.PHONE_SMART

    override fun get(): MainComponent =
        (application as MainApplication)
            .applicationComponent
            .mainComponent()
            .create(this)

    companion object {
        const val TAG = "MainActivity"
        const val DURATION = 300
    }
}