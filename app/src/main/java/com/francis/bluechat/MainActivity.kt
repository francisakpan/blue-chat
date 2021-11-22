package com.francis.bluechat

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.francis.bluechat.application.MainApplication
import com.francis.bluechat.utils.missingSystemFeature

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as MainApplication)
            .applicationComponent
            .inject(this)

        checkBluetoothFeature()
    }

    private fun checkBluetoothFeature() {
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH) }?.also {
            AlertDialog.Builder(this).apply {
                setTitle("Missing Feature")
                setMessage("This device does not support bluetooth technology.")
                setPositiveButton("OK") { d, _ ->
                    d.dismiss()
                    finish()
                }
                create()
            }.show()
        }
    }
}