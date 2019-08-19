package com.mbakgun.mobile.di.modules

import android.app.Application
import android.content.Context
import com.mbakgun.mobile.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by burakakgun on 8.06.2019.
 */
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideApplication(app: App): Application = app

    @Provides
    @Singleton
    fun provideApplicationContext(app: App): Context = app.applicationContext
}
