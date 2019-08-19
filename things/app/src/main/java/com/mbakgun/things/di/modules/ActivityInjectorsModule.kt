package com.mbakgun.things.di.modules

import com.mbakgun.things.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by burakakgun on 8.06.2019.
 */
@Module
abstract class ActivityInjectorsModule {

    @ContributesAndroidInjector
    abstract fun mainActivityInjector(): MainActivity
}
