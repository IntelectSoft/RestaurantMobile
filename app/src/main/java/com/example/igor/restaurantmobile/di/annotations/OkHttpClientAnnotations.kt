package com.example.igor.restaurantmobile.di.annotations

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OkHttpAuthInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OkHttpNoAuthInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OkHttpPerformantInterceptor