package com.mbakgun.mobile.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import javax.inject.Inject
import kotlin.reflect.KClass

typealias ViewModelInjectionField<T> = dagger.Lazy<T>

class InjectionViewModelProvider<VM : ViewModel> @Inject constructor(
    private val lazyViewModel: dagger.Lazy<VM>
) {

    @Suppress("UNCHECKED_CAST")
    @SuppressWarnings("unchecked")
    private val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>) = lazyViewModel.get() as T
    }

    fun <ACTIVITY : FragmentActivity> get(activity: ACTIVITY, viewModelClass: KClass<VM>) =
        ViewModelProviders.of(activity, viewModelFactory).get(viewModelClass.java)

    fun <FRAGMENT : Fragment> get(fragment: FRAGMENT, viewModelClass: KClass<VM>) =
        ViewModelProviders.of(fragment, viewModelFactory).get(viewModelClass.java)
}
