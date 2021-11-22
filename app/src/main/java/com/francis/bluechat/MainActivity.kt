package com.francis.bluechat

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.francis.bluechat.application.MainApplication
import com.francis.bluechat.utils.missingSystemFeature
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(
            MainActivityViewModel::class.java
        )
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as MainApplication)
            .applicationComponent
            .inject(this)

        checkBluetoothFeature()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun checkBluetoothFeature() {
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH) }?.also {
            AlertDialog.Builder(this).apply {
                setTitle("Missing Feature")
                setMessage("This device does not support bluetooth technology.")
                setPositiveButton("OK") { d, _ ->
                    d.dismiss()
                }
                setOnDismissListener { d ->
                    finish()
                }
                create()
            }.show()
        }
    }
}