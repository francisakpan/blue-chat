package com.francis.bluechat.dagger

import com.francis.bluechat.dagger.ViewModelFactory.ViewModelBuilderModule
import dagger.Module

@Module(includes = [ViewModelBuilderModule::class])
abstract class ApplicationModule