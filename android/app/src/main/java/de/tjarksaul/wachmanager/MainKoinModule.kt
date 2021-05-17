package de.tjarksaul.wachmanager

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val mainKoinModule = module {
    viewModel(override = true) {
        MainViewModel(
            sharedPreferences = androidContext().getSharedPreferences(
                BuildConfig.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE
            )
        )
    }
}