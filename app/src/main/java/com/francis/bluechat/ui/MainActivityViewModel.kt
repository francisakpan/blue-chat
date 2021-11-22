package com.francis.bluechat.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.francis.bluechat.application.MainApplication
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {


}