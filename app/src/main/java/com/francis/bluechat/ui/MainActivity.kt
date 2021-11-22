package com.francis.bluechat.ui

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.francis.bluechat.R
import com.francis.bluechat.application.MainApplication
import com.francis.bluechat.databinding.ActivityMainBinding
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

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        (application as MainApplication)
            .applicationComponent
            .inject(this)

        checkBluetoothFeature()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
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
                setOnDismissListener { d ->
                    finish()
                }
                create()
            }.show()
        }
    }
}