package md.edi.mobilewaiter.controllers

import android.app.Activity
import android.app.Application
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.data.database.ApplicationDb
import md.edi.mobilewaiter.utils.ContextManager
import java.lang.ref.WeakReference

@HiltAndroidApp
class App : Application() {
    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
        ContextManager.injectApplicationContext(this)

        ApplicationDb.getInstance(this)
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
        builder.setPopUpTo(destinationIdRes, popInclusive, false)
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
