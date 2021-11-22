package com.francis.bluechat.application

import android.app.Application
import com.francis.bluechat.dagger.ApplicationComponent
import com.francis.bluechat.dagger.DaggerApplicationComponent

class MainApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = getApplicationComponent(this)
    }

    private fun getApplicationComponent(application: MainApplication): ApplicationComponent {
        return DaggerApplicationComponent
            .factory()
            .create(application)
    }
}