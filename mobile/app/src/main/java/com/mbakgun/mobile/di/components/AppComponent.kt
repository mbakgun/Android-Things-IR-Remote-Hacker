package com.mbakgun.mobile.di.components

import com.mbakgun.mobile.App
import com.mbakgun.mobile.di.modules.ActivityInjectorsModule
import com.mbakgun.mobile.di.modules.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by burakakgun on 8.06.2019.
 */
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityInjectorsModule::class,
        AppModule::class]
)
@SuppressWarnings("unchecked")
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}
