package com.francis.bluechat.ui.dagger

import com.francis.bluechat.ui.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent

@MainScope
@Subcomponent(modules = [MainModule::class])
interface MainComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance activity: MainActivity) : MainComponent
    }

    fun inject(target: MainActivity)
}