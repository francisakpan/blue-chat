package com.francis.bluechat.ui.dagger

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import com.francis.bluechat.dagger.ViewModelFactory
import com.francis.bluechat.ui.MainActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {

    @Keep
    @Binds
    @IntoMap
    @ViewModelFactory.ViewModelKey(MainActivityViewModel::class)
    abstract fun bindsMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel
}