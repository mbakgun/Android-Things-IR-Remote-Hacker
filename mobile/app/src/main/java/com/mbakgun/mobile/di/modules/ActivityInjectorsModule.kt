package com.mbakgun.mobile.di.modules

import com.mbakgun.mobile.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by burakakgun on 8.06.2019.
 */
@Module
abstract class ActivityInjectorsModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun mainActivityInjector(): MainActivity
}
