package com.francis.bluechat.dagger

import android.app.Application
import com.francis.bluechat.ui.dagger.MainComponent
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance instance: Application): ApplicationComponent
    }

    fun mainComponent() : MainComponent.Factory
}