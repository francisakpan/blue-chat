package com.francis.bluechat.dagger

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import com.francis.bluechat.MainActivityViewModel
import com.francis.bluechat.dagger.ViewModelFactory.ViewModelBuilderModule
import com.francis.bluechat.dagger.ViewModelFactory.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelBuilderModule::class])
abstract class ApplicationModule {

    @Keep
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindsMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel
}