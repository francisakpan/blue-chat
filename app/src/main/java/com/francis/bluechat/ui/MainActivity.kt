package com.francis.bluechat.ui

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.francis.bluechat.R
import com.francis.bluechat.application.MainApplication
import com.francis.bluechat.databinding.ActivityMainBinding
import com.francis.bluechat.utils.SnackBarUtil
import com.francis.bluechat.utils.missingSystemFeature
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(
            MainActivityViewModel::class.java
        )
    }

    private val bluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Bluetooth Launcher: ${Activity.RESULT_OK}")
                TODO("Check for available devices.")
            } else {
                SnackBarUtil.instance?.showSnackBar(this, "Error turning on bluetooth")
            }
        }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        (application as MainApplication)
            .applicationComponent
            .inject(this)

        checkBluetoothFeature()
        checkToEnable()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.visibility -> {

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
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter != null && !adapter.isEnabled) {
            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).let { intent ->
                bluetoothLauncher.launch(intent)
            }
        } else if (adapter.isEnabled) {
            TODO("Check for available devices.")
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}