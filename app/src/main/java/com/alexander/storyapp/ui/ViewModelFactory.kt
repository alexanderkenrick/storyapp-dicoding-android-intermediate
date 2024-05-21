package com.alexander.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexander.storyapp.ui.auth.LoginViewModel
import com.alexander.storyapp.ui.auth.RegisterViewModel
import com.alexander.storyapp.ui.home.HomeViewModel
import com.alexander.storyapp.ui.upload.UploadViewModel
import com.alexander.storyapp.ui.welcome.WelcomeViewModel
import com.alexander.storyapp.utils.Injection

class ViewModelFactory internal constructor(
    private val context: Context,
//    private val pref: AuthPreferences
) : ViewModelProvider.NewInstanceFactory() {
//    companion object {
//        @Volatile
//        private var INSTANCE: ViewModelFactory? = null
//
//        @JvmStatic
//        fun getInstance(application: Application): ViewModelFactory {
//            if (INSTANCE == null) {
//                synchronized(ViewModelFactory::class.java) {
//                    INSTANCE = ViewModelFactory(
//                        application,
//                        pref = AuthPreferences.getInstance(application.dataStore)
//                    )
//                }
//            }
//            return INSTANCE as ViewModelFactory
//        }
//    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(Injection.provideRepository(context)) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(Injection.provideRepository(context)) as T
        } else if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(Injection.provideRepository(context)) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(Injection.provideRepository(context)) as T
        }else if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(Injection.provideRepository(context)) as T
        }
//        else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
//            return FavoriteViewModel(mApplication) as T
//        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
//            return DetailViewModel(mApplication) as T
//        } else if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
//            return ThemeViewModel(pref) as T
//        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}