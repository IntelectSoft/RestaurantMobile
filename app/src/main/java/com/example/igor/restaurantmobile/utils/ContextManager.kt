package com.example.igor.restaurantmobile.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

@SuppressLint("StaticFieldLeak") //Application context can be injected through such kind of links.
object ContextManager {

    private var injectedContext: Context? = null

    private var context : Context? = null

    private var activity: WeakReference<Activity> = WeakReference(null)

    fun retrieveApplicationContext() = injectedContext ?: throw Exception("Application is dead")

    fun retrieveContext() = context

    fun retrieveActivityContext() = activity

    fun injectApplicationContext(application: Application) {
        injectedContext = application.applicationContext
    }

    fun injectActivityContext(activity: Activity) {
        this.activity = WeakReference(activity)
    }
    fun injectContext(context: Context) {
        this.context = context
    }
}