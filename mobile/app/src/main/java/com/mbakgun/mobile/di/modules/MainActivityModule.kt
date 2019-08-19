package com.mbakgun.mobile.di.modules

import com.mbakgun.mobile.di.InjectionViewModelProvider
import com.mbakgun.mobile.di.qualifiers.ViewModelInjection
import com.mbakgun.mobile.ui.MainActivity
import com.mbakgun.mobile.ui.MainActivityVM
import dagger.Module
import dagger.Provides

/**
 * Created by burakakgun on 8.06.2019.
 */
@Module
class MainActivityModule {

    @Provides
    @ViewModelInjection
    fun provideMainActivityVM(
        activity: MainActivity,
        viewModelProvider: InjectionViewModelProvider<MainActivityVM>
    ) = viewModelProvider.get(activity, MainActivityVM::class)
}
