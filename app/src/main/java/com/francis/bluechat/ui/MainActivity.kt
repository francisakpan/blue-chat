package com.francis.bluechat.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.google.android.material.switchmaterial.SwitchMaterial
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity(), Provider<MainComponent> {

    private lateinit var binding: ActivityMainBinding
    private lateinit var switch: SwitchMaterial

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

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
                switch.isChecked = false
                SnackBarUtil.instance?.showSnackBar(this, "Error turning on bluetooth")
            }
        }

    private val bluetoothDiscoverabilityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                DURATION -> {
//                    TODO("")
                }
                Activity.RESULT_CANCELED -> {
//                    TODO("")
                }
            }
        }

    private val locationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it == true }) {
                bluetoothAdapter.startDiscovery().also { isOn ->
                    binding.swipeLayout.isRefreshing = isOn
                }
            } else {
                SnackBarUtil.instance?.showSnackBar(
                    this,
                    "Turn on location to discover bluetooth devices around you"
                ) {
                    checkLocationPermission()
                }
            }
        }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    if (device?.let { isPhone(it) } == true) {
                        val name = device.name ?: return
                        val address = device.address ?: return

                        with(adapter) {
                            val devices = currentList.toMutableSet().apply {
                                add(Device(name, address))
                            }
                            submitList(devices.toList())
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    binding.swipeLayout.isRefreshing = true
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    binding.swipeLayout.isRefreshing = false
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        get().inject(this)

        binding.recyclerView.adapter = adapter

        checkBluetoothFeature()
        registerFoundReceiver()
        onSwipeRefreshLayoutInflated()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun registerFoundReceiver() {
        // Register for broadcasts when a device is discovered.
        IntentFilter().let { filter ->
            filter.addAction(BluetoothDevice.ACTION_FOUND)
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            registerReceiver(receiver, filter)
        }
    }

    private fun onSwipeRefreshLayoutInflated() {
        with(binding.swipeLayout) {
            setOnRefreshListener {
                if (isRefreshing) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        checkLocationPermission()
                    }

                    if (bluetoothAdapter.isDiscovering) bluetoothAdapter.cancelDiscovery()

                    bluetoothAdapter.startDiscovery().also { isOn ->
                        isRefreshing = isOn
                    }
                } else if (!bluetoothAdapter.isEnabled) {
                    isRefreshing = false
                    SnackBarUtil.instance?.showSnackBar(
                        this@MainActivity,
                        "Bluetooth is not turned on"
                    ) {
                        turnOn()
                    }
                }
            }
        }
    }

    private fun checkLocationPermission() {
        (getSystemService(Context.LOCATION_SERVICE) as LocationManager).also {
            if (!it.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        setSwitchListener(menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setSwitchListener(menu: Menu?) {
        val menuItem = menu!!.findItem(R.id.power)
        switch = menuItem.actionView as SwitchMaterial
        switch.isChecked = bluetoothAdapter.isEnabled
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkToEnable()
            else bluetoothAdapter.disable()
        }
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

    private fun turnOn() {
        Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).let { intent ->
            bluetoothLauncher.launch(intent)
        }
    }

    private fun checkToEnable() {
        if (!bluetoothAdapter.isEnabled) {
            turnOn()
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