package com.francis.bluechat.dagger

import android.app.Application
import com.francis.bluechat.MainActivity
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance instance: Application): ApplicationComponent
    }

    fun inject(target: MainActivity)
}