package com.example.igor.restaurantmobile.controllers

import android.app.Application
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.example.igor.restaurantmobile.R
import com.example.igor.restaurantmobile.utils.ContextManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

@HiltAndroidApp
class App : Application() {
    companion object {
        lateinit var instance: App
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        ContextManager.injectApplicationContext(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    fun navigateToAnimated(navController: NavController,navDirections: NavDirections) {
        navController.navigate(navDirections, getNavOptionsRightToLeft(getNavBuilder()))
    }

    fun navigatePopBackStack(
        navController: NavController,
        @IdRes destinationIdRes: Int,
        popInclusive: Boolean = false
    ): Boolean {
        return navController.popBackStack(destinationIdRes, popInclusive)
    }

    fun navigateToAnimatedPopBackStack(
        navController: NavController,
        navDirections: NavDirections,
        @IdRes destinationIdRes: Int,
        popInclusive: Boolean = false
    ) {
        val builder = getNavBuilder()
        builder.setPopUpTo(destinationIdRes, popInclusive)
        navController.navigate(
            navDirections,
            getNavOptionsRightToLeft(builder)
        )
    }


    private fun getNavBuilder(): NavOptions.Builder {
        return NavOptions.Builder()

    }

    private fun getNavOptionsRightToLeft(builder: NavOptions.Builder): NavOptions? {
        return builder
            .setEnterAnim(R.anim.h_fragment_enter)
            .setExitAnim(R.anim.h_fragment_pop_exit)
            .setPopEnterAnim(R.anim.h_fragment_pop_enter)
            .setPopExitAnim(R.anim.h_fragment_exit)
            .build()
    }
}
